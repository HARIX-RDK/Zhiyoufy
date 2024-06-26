package zhiyoufyworker

import (
	"encoding/json"
	"errors"
	"fmt"

	"github.com/go-stomp/stomp/v3"
	log "github.com/sirupsen/logrus"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/api"
	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/collectionutil"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
)

const (
	destinationStartJobChildRunRsp  = "/app/start-job-child-run-rsp"
	destinationStopJobChildRunRsp   = "/app/stop-job-child-run-rsp"
	destinationJobChildRunResultInd = "/app/job-child-run-result-ind"
)

type jobManager struct {
	logPrefix string
	worker    *ZhiyoufyWorker

	// 状态相关
	jobRunner         *jms.JobRunner
	taskStopJobRunner *TaskStopJobRunner
	needSendResultInd bool

	serverRunId   string
	startJobReq   *jmsStartJobChildRunReq
	resultInd     *jmsJobChildRunResultInd
	jobFinishData *jms.EventJobFinishData
}

func newJobManager(worker *ZhiyoufyWorker) *jobManager {
	manager := &jobManager{
		logPrefix: "jobManager",
		worker:    worker,
	}

	return manager
}

func (manager *jobManager) onEventTaskStopJobRunnerEnd() {
	manager.taskStopJobRunner = nil

	manager.sendResultIndWhenAllDone()
}

func (manager *jobManager) onBusJmsEventJobFinish(data *jms.EventJobFinishData) {
	event := WorkerEvent{
		Type: eventJmsJobFinish,
		Data: data,
	}
	manager.worker.eventChan <- event
}

func (manager *jobManager) onJmsEventJobFinish(data *jms.EventJobFinishData) {
	if manager.taskStopJobRunner == nil {
		manager.jobRunner.Stop()
	}
	manager.jobFinishData = data

	manager.sendResultIndWhenAllDone()
}

func (manager *jobManager) onRegisterResult(registerResult *registerResult) {
	logPrefix := manager.logPrefix + ".onRegisterResult"

	if manager.serverRunId == registerResult.ServerRunId {
		return
	}

	manager.serverRunId = registerResult.ServerRunId

	if manager.jobRunner != nil && manager.taskStopJobRunner == nil {
		log.Infof("%s stop old run as serverRunId changed to %s", logPrefix, manager.serverRunId)

		taskStopJobRunner := &TaskStopJobRunner{
			ResultChan: manager.worker.eventChan,
			JobRunner:  manager.jobRunner,
		}
		go taskStopJobRunner.Run()

		manager.taskStopJobRunner = taskStopJobRunner

		manager.needSendResultInd = false
	}
}

func (manager *jobManager) onStartJobChildRunReq(stompMsg *stomp.Message) {
	logPrefix := manager.logPrefix + ".onStartJobChildRunReq"

	worker := manager.worker
	var err error

	var req jmsStartJobChildRunReq
	if err = json.Unmarshal(stompMsg.Body, &req); err != nil {
		log.Errorf("%s json parse failed, err %s", logPrefix, err)
		return
	}

	log.Infof("%s req %+v", logPrefix, req)

	var errorInfo *api.ErrorInfo

	defer func() {
		rsp := manager.buildStartJobChildRunRsp(&req, errorInfo)

		body, _ := json.Marshal(rsp)

		err := worker.stompConn.Send(destinationStartJobChildRunRsp, "application/json", body)

		if errorInfo != nil {
			log.Errorf("%s failed %s, send startJobChildRunRsp, msgId %s, err %s",
				logPrefix, errorInfo.Detail, rsp.MessageId, err)
		} else {
			log.Infof("%s ok, send startJobChildRunRsp, msgId %s, err %s",
				logPrefix, rsp.MessageId, err)
		}
	}()

	if manager.jobRunner != nil {
		errorInfo = RES_ERR_RESOURCE_NOT_AVAILABLE.Clone().WithDetail("one jobRunner already active")
		return
	}

	var cfgMap map[string]any

	err = json.Unmarshal([]byte(req.ConfigComposite), &cfgMap)

	if err != nil {
		errorInfo = RES_ERR_INVALID_CONFIG.Clone().WithDetail(fmt.Sprintf("parse error %s", err))
		return
	}

	parallelNum := 0
	rawVal, err := collectionutil.DeepGet(cfgMap, "jobRunner.parallelNum")
	if err == nil {
		if parallelNumFloat, ok := rawVal.(float64); !ok {
			err = errors.New("jobRunner.parallelNum is not number")
		} else {
			parallelNum = int(parallelNumFloat)
			if parallelNum < 1 {
				err = errors.New("jobRunner.parallelNum is less than 1")
			}
		}
	}
	if err != nil {
		errorInfo = RES_ERR_INVALID_CONFIG.Clone().WithDetail(fmt.Sprintf("parse error %s", err))
		return
	}

	spawnIntervalMs := 0
	rawVal, err = collectionutil.DeepGet(cfgMap, "jobRunner.spawnIntervalMs")
	if err == nil {
		if spawnIntervalMsFloat, ok := rawVal.(float64); !ok {
			err = errors.New("jobRunner.spawnIntervalMs is not number")
		} else {
			spawnIntervalMs = int(spawnIntervalMsFloat)
			if spawnIntervalMs < 10 {
				err = errors.New("jobRunner.spawnIntervalMs is less than 10")
			}
		}
	}
	if err != nil {
		errorInfo = RES_ERR_INVALID_CONFIG.Clone().WithDetail(fmt.Sprintf("parse error %s", err))
		return
	}

	spawnNum := 0
	rawVal, err = collectionutil.DeepGet(cfgMap, "jobRunner.spawnNum")
	if err == nil {
		if spawnNumFloat, ok := rawVal.(float64); !ok {
			err = errors.New("jobRunner.spawnNum is not number")
		} else {
			spawnNum = int(spawnNumFloat)
			if spawnNum < 1 {
				err = errors.New("jobRunner.spawnNum is less than 1")
			}
		}
	}
	if err != nil {
		errorInfo = RES_ERR_INVALID_CONFIG.Clone().WithDetail(fmt.Sprintf("parse error %s", err))
		return
	}

	logGroupSize := 0
	rawVal, err = collectionutil.DeepGet(cfgMap, "jobRunner.logGroupSize")
	if err == nil {
		if logGroupSizeFloat, ok := rawVal.(float64); !ok {
			err = errors.New("jobRunner.logGroupSize is not number")
		} else {
			logGroupSize = int(logGroupSizeFloat)
			if logGroupSize < 1 {
				err = errors.New("jobRunner.logGroupSize is less than 1")
			}
		}
	}
	if err != nil {
		errorInfo = RES_ERR_INVALID_CONFIG.Clone().WithDetail(fmt.Sprintf("parse error %s", err))
		return
	}

	jobRunnerConfig := jms.JobRunnerConfig{
		RunnerIdx:              req.Index,
		ParallelNum:            parallelNum,
		SpawnIntervalMs:        spawnIntervalMs,
		SpawnNum:               spawnNum,
		ScriptFile:             req.JobPath,
		ScriptRunnerCustomizer: worker.config.ScriptRunnerCustomizer,
		LogGroupSize:           logGroupSize,
		CfgMap:                 cfgMap,
	}

	jobRunner := jms.NewJobRunner(jobRunnerConfig)
	jobRunner.AddEventListener(manager.onBusJmsEventJobFinish)

	jobRunner.Run()

	manager.startJobReq = &req
	manager.jobRunner = jobRunner
	manager.needSendResultInd = true
}

func (manager *jobManager) onUpdatePerfParallelNumReq(stompMsg *stomp.Message) {
	logPrefix := manager.logPrefix + ".onUpdatePerfParallelNumReq"

	var err error

	var req jmsUpdatePerfParallelNumReq
	if err = json.Unmarshal(stompMsg.Body, &req); err != nil {
		log.Errorf("%s json parse failed, err %s", logPrefix, err)
		return
	}

	log.Infof("%s req %+v", logPrefix, req)

	if req.PerfParallelNum < 0 {
		log.Errorf("%s invalid perfParallelNum %d", logPrefix, req.PerfParallelNum)
		return
	}

	if manager.jobRunner != nil && manager.taskStopJobRunner == nil && manager.jobFinishData == nil {
		data := &jms.EventUpdateParallelNumData{
			ParallelNum: req.PerfParallelNum,
		}
		manager.jobRunner.SendEvent(jms.EventUpdateParallelNum, data)
	}
}

func (manager *jobManager) onStopJobChildRunReq(stompMsg *stomp.Message) {
	logPrefix := manager.logPrefix + ".onStopJobChildRunReq"

	worker := manager.worker
	var err error

	var req jmsStopJobChildRunReq
	if err = json.Unmarshal(stompMsg.Body, &req); err != nil {
		log.Errorf("%s json parse failed, err %s", logPrefix, err)
		return
	}

	log.Infof("%s req %+v", logPrefix, req)

	var errorInfo *api.ErrorInfo

	defer func() {
		if errorInfo == nil {
			return
		}

		rsp := manager.buildStopJobChildRunRsp(&req, errorInfo)

		body, _ := json.Marshal(rsp)

		err := worker.stompConn.Send(destinationStopJobChildRunRsp, "application/json", body)

		log.Errorf("%s failed %s, send stopJobChildRunRsp, msgId %s, err %s",
			logPrefix, errorInfo.Detail, rsp.MessageId, err)
	}()

	if manager.resultInd != nil {
		errorInfo = RES_ERR_INVALID_STATE.Clone().WithDetail("resultInd != nil")
		return
	}

	if manager.jobFinishData != nil {
		errorInfo = RES_ERR_INVALID_STATE.Clone().WithDetail("jobFinishData != nil")
		return
	}

	if manager.taskStopJobRunner != nil {
		log.Infof("%s ignore as taskStopJobRunner != nil", logPrefix)
		return
	}

	taskStopJobRunner := &TaskStopJobRunner{
		ResultChan: worker.eventChan,
		JobRunner:  manager.jobRunner,
	}
	go taskStopJobRunner.Run()

	manager.taskStopJobRunner = taskStopJobRunner
}

func (manager *jobManager) onJobChildRunResultRsp(stompMsg *stomp.Message) {
	logPrefix := manager.logPrefix + ".onJobChildRunResultRsp"

	if manager.resultInd == nil {
		log.Errorf("%s ignore, no pending resultInd", logPrefix)
		return
	}

	var err error

	var resultRsp jmsJobChildRunResultRsp
	if err = json.Unmarshal(stompMsg.Body, &resultRsp); err != nil {
		log.Errorf("%s json parse failed, err %s", logPrefix, err)
		return
	}

	log.Infof("%s resultRsp %+v", logPrefix, resultRsp)

	if resultRsp.MessageId != manager.resultInd.MessageId {
		log.Errorf("%s mismatch MessageId, local %s", logPrefix, manager.resultInd.MessageId)
		return
	}

	manager.resultInd = nil
}

func (manager *jobManager) buildStartJobChildRunRsp(req *jmsStartJobChildRunReq,
	errorInfo *api.ErrorInfo) *jmsStartJobChildRunRsp {
	rsp := &jmsStartJobChildRunRsp{
		Error:     errorInfo,
		RunGuid:   req.RunGuid,
		Index:     req.Index,
		MessageId: randutil.GenerateShortHexId(),
	}

	return rsp
}

func (manager *jobManager) buildStopJobChildRunRsp(req *jmsStopJobChildRunReq,
	errorInfo *api.ErrorInfo) *jmsStopJobChildRunRsp {
	rsp := &jmsStopJobChildRunRsp{
		Error:     errorInfo,
		RunGuid:   req.RunGuid,
		Index:     req.Index,
		MessageId: randutil.GenerateShortHexId(),
	}

	return rsp
}

func (manager *jobManager) sendResultIndWhenAllDone() {
	logPrefix := manager.logPrefix + ".sendResultIndWhenAllDone"

	worker := manager.worker
	jobReq := manager.startJobReq
	data := manager.jobFinishData

	if manager.taskStopJobRunner != nil {
		return
	}

	manager.startJobReq = nil
	manager.jobRunner = nil
	manager.jobFinishData = nil

	if !manager.needSendResultInd {
		return
	}

	resultInd := &jmsJobChildRunResultInd{
		MessageId:    randutil.GenerateShortHexId(),
		RunGuid:      jobReq.RunGuid,
		Index:        jobReq.Index,
		EndOk:        true,
		JobOutputUrl: worker.config.JobOutputUrl,
	}

	if data.Err == nil {
		resultInd.ResultOk = true
		resultInd.Passed = true
		resultInd.ChildStatusCntMap = data.ChildStatusCntMap
	}

	log.Infof("%s resultInd %+v", logPrefix, resultInd)

	manager.resultInd = resultInd

	body, _ := json.Marshal(resultInd)

	err := worker.stompConn.Send(destinationJobChildRunResultInd, "application/json", body)

	if data.Err != nil {
		log.Errorf("%s failed %s, send jobChildRunResultInd, msgId %s, err %s",
			logPrefix, data.Err, resultInd.MessageId, err)
	} else {
		log.Infof("%s ok, send jobChildRunResultInd, msgId %s, err %s",
			logPrefix, resultInd.MessageId, err)
	}
}
