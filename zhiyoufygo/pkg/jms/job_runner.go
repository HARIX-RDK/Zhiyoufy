package jms

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"os"
	"sync"
	"sync/atomic"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/bus"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/scriptutil"
)

var (
	ErrInvalidState = errors.New("invalid state")
)

type JobRunnerConfig struct {
	EventBus               bus.Bus                          `json:"-"`
	RunnerIdx              int                              `json:"runnerIdx"`
	ParallelNum            int                              `json:"parallelNum"`     // 并行运行数
	SpawnIntervalMs        int                              `json:"spawnIntervalMs"` // 启动新运行间隔
	SpawnNum               int                              `json:"spawnNum"`        // 每次SpawnIntervalMs间隔后启动的数量
	ScriptFile             string                           `json:"scriptFile"`      // 脚本路径
	ScriptContent          []byte                           // 脚本内容，与ScriptFile互斥
	CfgMap                 map[string]any                   `json:"cfgMap"` // json配置，脚本里的command可以从配置中提取信息
	ScriptRunnerCustomizer script.ScriptJobRunnerCustomizer // 主要是注册command handler，从而能支持目标command
	LogGroupSize           int                              `json:"logGroupSize"` // 用于控制log的量，每LogGroupSize个运行的第一个会允许输出info级别log，其它则只能输出error级别
}

type JobRunner struct {
	bus.Bus

	config      JobRunnerConfig
	runIdPrefix string

	// 通用事件处理相关
	eventChan chan JmsEvent
	running   atomic.Bool     // 是否调用Run
	runMutex  *sync.Mutex     // 用于同步Run, Stop
	runWg     *sync.WaitGroup // Stop时候用于同步等待
	stopping  bool

	spawnTimer         *time.Timer
	spawnTimerCancel   chan struct{}
	parallelNum        int
	spawnedNum         int
	dynamicParallelNum bool
	finishEventSent    bool

	cfgCtx               core.IConfigContext
	rootCtx              context.Context
	rootCtxCancel        context.CancelFunc
	finallyRootCtx       context.Context
	finallyRootCtxCancel context.CancelFunc
	scriptJobRunner      *script.ScriptJobRunner
	scriptRootNode       *script.ScriptRootNode
	taskChildJobRunMap   map[int]*taskChildJobRun
	childStatusCntMap    map[script.ScriptJobStatus]int
}

func NewJobRunner(config JobRunnerConfig) *JobRunner {
	jobRunner := &JobRunner{
		Bus:                config.EventBus,
		config:             config,
		runIdPrefix:        randutil.GenerateShortHexId(),
		runMutex:           &sync.Mutex{},
		runWg:              &sync.WaitGroup{},
		parallelNum:        config.ParallelNum,
		taskChildJobRunMap: make(map[int]*taskChildJobRun),
		childStatusCntMap:  make(map[script.ScriptJobStatus]int),
		cfgCtx:             core.NewSimpleConfigContext(config.CfgMap),
	}
	if jobRunner.Bus == nil {
		jobRunner.Bus = bus.New()
	}
	if jobRunner.config.LogGroupSize <= 0 {
		jobRunner.config.LogGroupSize = 200
	}
	return jobRunner
}

func (jobRunner *JobRunner) Run() error {
	logPrefix := "JobRunner.Run"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	jobRunner.runMutex.Lock()
	defer jobRunner.runMutex.Unlock()

	if jobRunner.running.Load() {
		return ErrInvalidState
	}
	jobRunner.running.Store(true)
	jobRunner.runWg = &sync.WaitGroup{}
	jobRunner.runWg.Add(1)
	jobRunner.eventChan = make(chan JmsEvent, 10)

	rootCtx, rootCtxCancel := context.WithCancel(context.Background())
	jobRunner.rootCtx = rootCtx
	jobRunner.rootCtxCancel = rootCtxCancel

	finallyRootCtx, finallyRootCtxCancel := context.WithCancel(context.Background())
	jobRunner.finallyRootCtx = finallyRootCtx
	jobRunner.finallyRootCtxCancel = finallyRootCtxCancel

	go jobRunner.doRun()
	jobRunner.SendEvent(eventStart, nil)

	return nil
}

func (jobRunner *JobRunner) Stop() error {
	logPrefix := "JobRunner.Stop"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	jobRunner.runMutex.Lock()
	defer jobRunner.runMutex.Unlock()

	if !jobRunner.running.Load() {
		return ErrInvalidState
	}

	jobRunner.SendEvent(eventStop, nil)
	jobRunner.runWg.Wait()
	jobRunner.running.Store(false)
	jobRunner.stopping = false

	return nil
}

func (jobRunner *JobRunner) SendEvent(eventType JmsEventType, data any) {
	event := JmsEvent{
		Type: eventType,
		Data: data,
	}
	jobRunner.eventChan <- event
}

func (jobRunner *JobRunner) doRun() {
	for event := range jobRunner.eventChan {
		jobRunner.processEvent(event)

		if jobRunner.stopping {
			// 等待goroutine结束，代表不会有内部新消息
			if jobRunner.spawnTimer != nil || len(jobRunner.taskChildJobRunMap) > 0 {
				continue
			}

			break
		}
	}

	jobRunner.runWg.Done()
}

func (jobRunner *JobRunner) processEvent(event JmsEvent) {
	logPrefix := "JobRunner.processEvent"

	log.Infof("%s Enter %s", logPrefix, event.Type)
	defer log.Infof("%s Leave %s", logPrefix, event.Type)

	switch event.Type {
	case eventSpawnTimeout:
		jobRunner.onEventSpawnTimeout()
	case eventStart:
		jobRunner.onEventStart()
	case eventStop:
		jobRunner.onEventStop()
	case eventTaskChildJobRunEnd:
		jobRunner.onEventTaskChildJobRunEnd(event.Data.(*taskChildJobRun))
	case EventUpdateParallelNum:
		jobRunner.onEventUpdateParallelNum(event.Data.(*EventUpdateParallelNumData))
	}
}

func (jobRunner *JobRunner) onEventSpawnTimeout() {
	logPrefix := "JobRunner.onEventSpawnTimeout"

	jobRunner.spawnTimer = nil
	jobRunner.spawnTimerCancel = nil

	if jobRunner.stopping {
		log.Infof("%s already stopping", logPrefix)
		return
	}

	if jobRunner.finishEventSent {
		log.Infof("%s already finishEventSent", logPrefix)
		return
	}

	jobRunner.adjustForParallelNum()
}

func (jobRunner *JobRunner) onEventStart() {
	logPrefix := "JobRunner.onEventStart"

	var err error
	eventData := &EventJobFinishData{}

	cfg := &jobRunner.config
	runner := scriptutil.NewScriptJobRunner()

	if cfg.ScriptRunnerCustomizer != nil {
		err = cfg.ScriptRunnerCustomizer(runner)

		if err != nil {
			eventData.Err = err
			jobRunner.Publish(eventData)
			return
		}
	}

	if cfg.ParallelNum < 1 {
		err = fmt.Errorf("ParallelNum %d is less than 1", cfg.ParallelNum)
		log.Errorf("%s err %s", logPrefix, err)
		eventData.Err = err
		jobRunner.Publish(eventData)
		return
	}

	if cfg.SpawnNum < 1 {
		err = fmt.Errorf("SpawnNum %d is less than 1", cfg.SpawnNum)
		log.Errorf("%s err %s", logPrefix, err)
		eventData.Err = err
		jobRunner.Publish(eventData)
		return
	}

	if cfg.SpawnIntervalMs < 10 {
		err = fmt.Errorf("SpawnIntervalMs %d is less than 10", cfg.SpawnIntervalMs)
		log.Errorf("%s err %s", logPrefix, err)
		eventData.Err = err
		jobRunner.Publish(eventData)
		return
	}

	var content []byte

	if cfg.ScriptContent != nil {
		content = cfg.ScriptContent
	} else {
		content, err = os.ReadFile(cfg.ScriptFile)

		if err != nil {
			err = fmt.Errorf("read script %s failed, %w", cfg.ScriptFile, err)
			log.Errorf("%s err %s", logPrefix, err)
			eventData.Err = err
			jobRunner.Publish(eventData)
			return
		}
	}

	scriptRootNode, err := runner.ParseScript(string(content))

	if err != nil {
		err = fmt.Errorf("parse script %s failed, %w", cfg.ScriptFile, err)
		log.Errorf("%s err %s", logPrefix, err)
		eventData.Err = err
		jobRunner.Publish(eventData)
		return
	}

	jobRunner.scriptJobRunner = runner
	jobRunner.scriptRootNode = scriptRootNode

	spawnNum := cfg.SpawnNum

	if spawnNum > jobRunner.parallelNum {
		spawnNum = jobRunner.parallelNum
	}

	for idx := 0; idx < spawnNum; idx++ {
		jobRunner.startTaskChildJobRun(idx)
	}

	if spawnNum < jobRunner.parallelNum {
		jobRunner.startSpawnTimer()
	}
}

func (jobRunner *JobRunner) onEventStop() {
	jobRunner.stopping = true
	for _, task := range jobRunner.taskChildJobRunMap {
		task.runCtx.StopRequested.Store(true)
	}
	jobRunner.rootCtxCancel()

	if jobRunner.spawnTimerCancel != nil {
		close(jobRunner.spawnTimerCancel)
		jobRunner.spawnTimerCancel = nil
	}
}

func (jobRunner *JobRunner) onEventTaskChildJobRunEnd(taskChildJobRun *taskChildJobRun) {
	delete(jobRunner.taskChildJobRunMap, taskChildJobRun.idx)

	childStatus := taskChildJobRun.runCtx.JobStatus
	jobRunner.childStatusCntMap[childStatus] += 1

	okToFinish := false
	needRestart := false

	if jobRunner.stopping ||
		(!jobRunner.dynamicParallelNum && jobRunner.spawnedNum == jobRunner.parallelNum) {
		okToFinish = true
	} else if jobRunner.dynamicParallelNum && taskChildJobRun.idx < jobRunner.parallelNum {
		needRestart = true
	}

	if okToFinish {
		if len(jobRunner.taskChildJobRunMap) == 0 {
			jobRunner.finishEventSent = true

			eventData := &EventJobFinishData{
				ChildStatusCntMap: jobRunner.childStatusCntMap,
			}

			jobRunner.Publish(eventData)
		}
	} else if needRestart {
		jobRunner.startTaskChildJobRun(taskChildJobRun.idx)
	}
}

func (jobRunner *JobRunner) onEventUpdateParallelNum(data *EventUpdateParallelNumData) {
	logPrefix := "JobRunner.onEventUpdateParallelNum"

	if data.ParallelNum < 0 {
		log.Infof("%s ignore as invalid ParallelNum %v", logPrefix, data.ParallelNum)
		return
	}

	if jobRunner.stopping {
		log.Infof("%s ignore as already stopping", logPrefix)
		return
	}

	if jobRunner.finishEventSent {
		log.Infof("%s ignore as already finishEventSent", logPrefix)
		return
	}

	if !jobRunner.dynamicParallelNum {
		jobRunner.dynamicParallelNum = true
	}

	activeTaskLen := len(jobRunner.taskChildJobRunMap)
	log.Infof("%s oldParallelNum %v, newParallelNum %v, activeTaskLen %v",
		logPrefix, jobRunner.parallelNum, data.ParallelNum, activeTaskLen)

	jobRunner.parallelNum = data.ParallelNum

	if jobRunner.spawnTimer == nil {
		jobRunner.adjustForParallelNum()
	}
}

func (jobRunner *JobRunner) adjustForParallelNum() {
	logPrefix := "JobRunner.adjustForParallelNum"

	activeTaskLen := len(jobRunner.taskChildJobRunMap)
	log.Infof("%s dynamicParallelNum %v, activeTaskLen %v, spawnedNum %v, ParallelNum %v", logPrefix,
		jobRunner.dynamicParallelNum, activeTaskLen, jobRunner.spawnedNum, jobRunner.parallelNum)

	if !jobRunner.dynamicParallelNum {
		if jobRunner.spawnedNum >= jobRunner.parallelNum {
			log.Errorf("%s already spawned enough", logPrefix)
			return
		}

		spawnNum := jobRunner.config.SpawnNum
		startIdx := jobRunner.spawnedNum

		if spawnNum > (jobRunner.parallelNum - jobRunner.spawnedNum) {
			spawnNum = jobRunner.parallelNum - jobRunner.spawnedNum
		}

		for idx := startIdx; idx < (startIdx + spawnNum); idx++ {
			jobRunner.startTaskChildJobRun(idx)
		}

		if jobRunner.spawnedNum < jobRunner.parallelNum {
			jobRunner.startSpawnTimer()
		} else {
			log.Infof("%s adjust done", logPrefix)
		}

		return
	}

	parallelNum := jobRunner.parallelNum
	spawnNum := jobRunner.config.SpawnNum
	stoppingNum := 0

	for _, taskChildJobRun := range jobRunner.taskChildJobRunMap {
		if taskChildJobRun.runCtx.StopRequested.Load() {
			stoppingNum += 1
		}
	}

	if stoppingNum >= spawnNum {
		log.Infof("%s wait more time as already stoppingNum %v >= spawnNum %v", logPrefix,
			stoppingNum, spawnNum)
		jobRunner.startSpawnTimer()
		return
	}

	newStoppingNum := 0
	for idx, taskChildJobRun := range jobRunner.taskChildJobRunMap {
		if idx < parallelNum {
			continue
		}

		if !taskChildJobRun.runCtx.StopRequested.Load() {
			taskChildJobRun.runCtx.StopRequested.Store(true)
			taskChildJobRun.rootCtxCancel()

			newStoppingNum += 1

			if (stoppingNum + newStoppingNum) >= spawnNum {
				break
			}
		}
	}

	freeSpawnNum := spawnNum - (stoppingNum + newStoppingNum)

	log.Infof("%s freeSpawnNum %v, stoppingNum %v, newStoppingNum %v",
		logPrefix, freeSpawnNum, stoppingNum, newStoppingNum)

	if freeSpawnNum <= 0 {
		jobRunner.startSpawnTimer()
		return
	}

	newSpawnNum := 0
	for idx := 0; idx < parallelNum; idx++ {
		_, exist := jobRunner.taskChildJobRunMap[idx]

		if exist {
			continue
		}

		newSpawnNum += 1
		jobRunner.startTaskChildJobRun(idx)

		if newSpawnNum >= freeSpawnNum {
			break
		}
	}

	log.Infof("%s newSpawnNum %v", logPrefix, newSpawnNum)

	if stoppingNum > 0 || newStoppingNum > 0 || newSpawnNum > 0 {
		jobRunner.startSpawnTimer()
	} else {
		log.Infof("%s adjust done", logPrefix)
	}
}

func (jobRunner *JobRunner) startTaskChildJobRun(idx int) {
	cfg := &jobRunner.config

	runId := fmt.Sprintf("%s-%d", jobRunner.runIdPrefix, idx)
	runRootCtx, cancel := context.WithCancel(jobRunner.rootCtx)
	runFinallyRootCtx, finallyCancel := context.WithCancel(jobRunner.finallyRootCtx)
	client := &http.Client{Timeout: time.Duration(20) * time.Second, Transport: core.CustomTransport}
	runCtx := script.NewScriptRunContext(script.ScriptRunContextConfig{
		IConfigContext: jobRunner.cfgCtx,

		RunnerIdx:      cfg.RunnerIdx,
		ChildIdx:       idx,
		RunId:          runId,
		RootScriptNode: jobRunner.scriptRootNode,

		RootCtx:        runRootCtx,
		FinallyRootCtx: runFinallyRootCtx,
		HttpClient:     client,
	})

	if (idx % cfg.LogGroupSize) != 0 {
		runCtx.SetLogLevelThreshold(log.ErrorLevel)
	}

	taskChildJobRun := &taskChildJobRun{
		ResultChan: jobRunner.eventChan,

		runId:  runId,
		runner: jobRunner.scriptJobRunner,
		runCtx: runCtx,

		idx:                  idx,
		rootCtx:              runRootCtx,
		rootCtxCancel:        cancel,
		finallyRootCtx:       runFinallyRootCtx,
		finallyRootCtxCancel: finallyCancel,
	}
	jobRunner.taskChildJobRunMap[idx] = taskChildJobRun
	jobRunner.spawnedNum += 1

	go taskChildJobRun.run()
}

func (jobRunner *JobRunner) startSpawnTimer() {
	spawnTimer := time.NewTimer(time.Duration(jobRunner.config.SpawnIntervalMs) * time.Millisecond)
	spawnTimerCancel := make(chan struct{})
	jobRunner.spawnTimer = spawnTimer
	jobRunner.spawnTimerCancel = spawnTimerCancel
	go func() {
		cancelled := false
		select {
		case <-spawnTimer.C:
		case <-spawnTimerCancel:
			cancelled = true
		}
		eventSpawnTimeout := JmsEvent{
			Type: eventSpawnTimeout,
			Data: &eventSpawnTimeoutData{
				timer:     spawnTimer,
				cancelled: cancelled,
			},
		}
		jobRunner.eventChan <- eventSpawnTimeout
	}()
}
