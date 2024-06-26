package zhiyoufyworker

import (
	"context"
	"errors"
	"sync"
	"sync/atomic"

	"github.com/go-stomp/stomp/v3"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core/bus"
	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/stompchannel"
)

var (
	ErrInvalidState = errors.New("invalid state")
)

type ZhiyoufyWorkerConfig struct {
	EventBus               bus.Bus
	HttpPort               int    `json:"httpPort"`
	ServerAddr             string `json:"serverAddr"`
	WorkerApp              string `json:"workerApp"`
	WorkerGroup            string `json:"workerGroup"`
	GroupTokenName         string `json:"groupTokenName"`
	GroupTokenSecret       string `json:"groupTokenSecret"`
	WorkerName             string `json:"workerName"`
	JobOutputUrl           string `json:"jobOutputUrl"`
	AppRunId               string
	AppStartTimestamp      string
	ScriptRunnerCustomizer script.ScriptJobRunnerCustomizer
}

type ZhiyoufyWorker struct {
	bus.Bus

	config        ZhiyoufyWorkerConfig
	eventChan     chan WorkerEvent
	logPrefix     string
	rootCtx       context.Context
	rootCtxCancel context.CancelFunc // 用于Stop时候cancel进行中工作
	runMutex      *sync.Mutex        // 用于同步Run, Stop
	runWg         *sync.WaitGroup    // Stop时候用于同步等待

	// 状态相关
	running                   atomic.Bool // 是否调用Run
	channelController         *stompchannel.ChannelController
	channelState              stompchannel.ConnectState
	registerManager           *registerManager
	jobManager                *jobManager
	stompConn                 *stomp.Conn
	stopping                  bool
	taskReadStompMessage      *TaskReadStompMessage
	taskStopChannelController *TaskStopChannelController
}

func NewZhiyoufyWorker(config ZhiyoufyWorkerConfig) *ZhiyoufyWorker {
	worker := ZhiyoufyWorker{
		Bus:       config.EventBus,
		config:    config,
		logPrefix: "ZhiyoufyWorker",
		runMutex:  &sync.Mutex{},
	}

	if worker.Bus == nil {
		worker.Bus = bus.New()
	}

	return &worker
}

func (worker *ZhiyoufyWorker) Run() error {
	logPrefix := worker.logPrefix + ".Run"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	worker.runMutex.Lock()
	defer worker.runMutex.Unlock()

	if worker.running.Load() {
		return ErrInvalidState
	}
	worker.running.Store(true)
	worker.runWg = &sync.WaitGroup{}
	worker.runWg.Add(1)
	worker.eventChan = make(chan WorkerEvent, 10)
	worker.channelState = stompchannel.Disconnected
	worker.registerManager = newRegisterManager(worker)
	worker.jobManager = newJobManager(worker)

	ctx, cancel := context.WithCancel(context.Background())
	worker.rootCtx = ctx
	worker.rootCtxCancel = cancel

	go worker.doRun()
	worker.SendEvent(eventConnectToServer, nil)

	return nil
}

func (worker *ZhiyoufyWorker) Stop() error {
	logPrefix := worker.logPrefix + ".Stop"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	worker.runMutex.Lock()
	defer worker.runMutex.Unlock()

	if !worker.running.Load() {
		return ErrInvalidState
	}

	worker.SendEvent(eventStop, nil)
	worker.runWg.Wait()
	worker.running.Store(false)
	worker.stopping = false

	return nil
}

func (worker *ZhiyoufyWorker) SendEvent(eventType WorkerEventType, data any) {
	event := WorkerEvent{
		Type: eventType,
		Data: data,
	}
	worker.eventChan <- event
}

func (worker *ZhiyoufyWorker) doRun() {
	for event := range worker.eventChan {
		worker.processEvent(event)

		if worker.stopping {
			// 等待goroutine结束
			if worker.channelController != nil || worker.taskReadStompMessage != nil ||
				worker.taskStopChannelController != nil || worker.registerManager.registerTimer != nil {
				continue
			}

			break
		}
	}

	worker.runWg.Done()
}

func (worker *ZhiyoufyWorker) processEvent(event WorkerEvent) {
	logPrefix := worker.logPrefix + ".processEvent"

	log.Infof("%s Enter %s", logPrefix, event.Type)
	defer log.Infof("%s Leave %s", logPrefix, event.Type)

	switch event.Type {
	case eventConnectToServer:
		worker.onEventConnectToServer()
	case EventInStompMessage:
		worker.onEventInStompMessage(event)
	case eventJmsJobFinish:
		worker.jobManager.onJmsEventJobFinish(event.Data.(*jms.EventJobFinishData))
	case eventRegisterTimeout:
		worker.registerManager.onRegisterTimeout(event.Data.(*eventRegisterTimeoutData))
	case eventStompChannelStateUpdate:
		worker.onEventStompChannelStateUpdate(event)
	case eventStop:
		worker.onEventStop()
	case EventTaskReadStompMessageEnd:
		worker.onEventTaskReadStompMessageEnd()
	case EventTaskStopChannelControllerEnd:
		worker.channelController = nil
		worker.taskStopChannelController = nil
	case EventTaskStopJobRunnerEnd:
		worker.jobManager.onEventTaskStopJobRunnerEnd()
	}
}

func (worker *ZhiyoufyWorker) onEventConnectToServer() {
	config := stompchannel.ChannelControllerConfig{
		ServerAddr: worker.config.ServerAddr,
	}
	channelController := stompchannel.NewChannelController(config)
	if err := channelController.Run(); err != nil {
		panic(err)
	}
	channelController.AddEventListener(worker.onStompChannelEventStateUpdateData)

	worker.channelController = channelController
}

func (worker *ZhiyoufyWorker) onEventInStompMessage(event WorkerEvent) {
	logPrefix := worker.logPrefix + ".onEventInStompMessage"

	stompMsg := event.Data.(*stomp.Message)

	log.Infof("%s destination %s", logPrefix, stompMsg.Destination)

	switch stompMsg.Destination {
	case "/app/worker-register-rsp":
		worker.registerManager.onRegisterRsp(stompMsg)
		if worker.registerManager.registered {
			worker.jobManager.onRegisterResult(worker.registerManager.registerResult)
		}
	case "/app/start-job-child-run-req":
		worker.jobManager.onStartJobChildRunReq(stompMsg)
	case "/app/update-perf-parallel-num-req":
		worker.jobManager.onUpdatePerfParallelNumReq(stompMsg)
	case "/app/stop-job-child-run-req":
		worker.jobManager.onStopJobChildRunReq(stompMsg)
	case "/app/job-child-run-result-rsp":
		worker.jobManager.onJobChildRunResultRsp(stompMsg)
	}
}

func (worker *ZhiyoufyWorker) onEventStompChannelStateUpdate(event WorkerEvent) {
	logPrefix := worker.logPrefix + ".onEventStompChannelStateUpdate"

	eventData := event.Data.(*stompchannel.EventStateUpdateData)
	oldChannelState := worker.channelState
	worker.channelState = eventData.NewState

	log.Infof("%s newState %s", logPrefix, eventData.NewState)

	if eventData.NewState == stompchannel.Connected {
		worker.stompConn = eventData.StompConn

		if worker.taskReadStompMessage == nil {
			worker.doAfterStompChannelConnected()
		} else {
			log.Infof("%s will do after old taskReadStompMessage finish", logPrefix)
		}
	} else if oldChannelState == stompchannel.Connected {
		worker.registerManager.onChannelNotConnected()
	}
}

func (worker *ZhiyoufyWorker) onEventStop() {
	worker.stopping = true
	worker.rootCtxCancel()
	taskStopChannelController := &TaskStopChannelController{
		ResultChan:        worker.eventChan,
		ChannelController: worker.channelController,
	}
	worker.taskStopChannelController = taskStopChannelController
	go taskStopChannelController.Run()
	worker.registerManager.onStop()
}

func (worker *ZhiyoufyWorker) onEventTaskReadStompMessageEnd() {
	taskReadStompMessage := worker.taskReadStompMessage
	worker.taskReadStompMessage = nil

	if worker.channelState == stompchannel.Connected && taskReadStompMessage.StompConn != worker.stompConn {
		worker.doAfterStompChannelConnected()
	}
}

func (worker *ZhiyoufyWorker) onStompChannelEventStateUpdateData(data *stompchannel.EventStateUpdateData) {
	event := WorkerEvent{
		Type: eventStompChannelStateUpdate,
		Data: data,
	}
	worker.eventChan <- event
}

func (worker *ZhiyoufyWorker) doAfterStompChannelConnected() {
	err := worker.doTaskReadStompMessage()
	if err == nil {
		err = worker.registerManager.registerToServer()
	}
	if err != nil {
		data := &stompchannel.EventReconnectData{StompConn: worker.stompConn}
		worker.channelController.SendEvent(stompchannel.EventReconnect, data)
	}
}

func (worker *ZhiyoufyWorker) doTaskReadStompMessage() error {
	logPrefix := worker.logPrefix + ".doTaskReadStompMessage"

	log.Infof("%s Enter", logPrefix)

	stompConn := worker.stompConn

	subscription, err := stompConn.Subscribe(
		"", stomp.AckAuto, stomp.SubscribeOpt.Header(stomp.ReplyToHeader, "0"))
	if err != nil {
		log.Errorf("%s Subscribe failed %s", logPrefix, err)
		return err
	}

	taskReadStompMessage := &TaskReadStompMessage{
		ResultChan:   worker.eventChan,
		StompConn:    stompConn,
		Subscription: subscription,
	}
	worker.taskReadStompMessage = taskReadStompMessage

	go taskReadStompMessage.Run()

	return nil
}
