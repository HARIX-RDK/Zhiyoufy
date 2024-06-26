package zhiyoufyworker

import (
	"encoding/json"
	"time"

	"github.com/go-stomp/stomp/v3"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core/api"
)

type registerManager struct {
	worker          *ZhiyoufyWorker
	registerTimeout int

	// 状态相关
	registered          bool
	registerTimer       *time.Timer
	registerTimerCancel chan struct{}
	registerResult      *registerResult
}

type registerResult struct {
	Error       *api.ErrorInfo `json:"error"`
	ServerRunId string         `json:"serverRunId"`
}

func newRegisterManager(worker *ZhiyoufyWorker) *registerManager {
	manager := &registerManager{
		worker:          worker,
		registerTimeout: 20,
	}

	return manager
}

func (manager *registerManager) onChannelNotConnected() {
	logPrefix := "registerManager.onChannelNotConnected"

	log.Infof("%s Enter", logPrefix)

	if manager.registered {
		manager.registered = false

		eventRegisterStateUpdate := &EventRegisterStateUpdate{
			Registered: manager.registered,
		}
		manager.worker.Publish(eventRegisterStateUpdate)
	}

	if manager.registerTimerCancel != nil {
		close(manager.registerTimerCancel)
		manager.registerTimerCancel = nil
	}
}

func (manager *registerManager) onRegisterRsp(stompMsg *stomp.Message) {
	logPrefix := "registerManager.onRegisterRsp"

	log.Infof("%s Enter", logPrefix)

	if manager.registerTimerCancel != nil {
		close(manager.registerTimerCancel)
		manager.registerTimerCancel = nil
	}

	var result registerResult
	json.Unmarshal(stompMsg.Body, &result)

	if result.Error != nil {
		log.Errorf("%s rsp error %v", logPrefix, result.Error)
		return
	}

	manager.registered = true
	manager.registerResult = &result

	eventRegisterStateUpdate := &EventRegisterStateUpdate{
		Registered: manager.registered,
	}
	manager.worker.Publish(eventRegisterStateUpdate)

	log.Infof("%s register ok", logPrefix)
}

func (manager *registerManager) onRegisterTimeout(data *eventRegisterTimeoutData) {
	logPrefix := "registerManager.onRegisterTimeout"

	log.Infof("%s Enter cancelled %v", logPrefix, data.cancelled)

	manager.registerTimer = nil
	manager.registerTimerCancel = nil

	if !data.cancelled && !manager.registered {
		log.Errorf("%s timeout not registered", logPrefix)
	}
}

func (manager *registerManager) onStop() {
	if manager.registerTimerCancel != nil {
		close(manager.registerTimerCancel)
		manager.registerTimerCancel = nil
	}
}

func (manager *registerManager) registerToServer() error {
	logPrefix := "registerManager.registerToServer"

	worker := manager.worker
	workerConfig := &worker.config
	destination := "/app/worker-register"
	msgBody := make(map[string]any)

	msgBody["workerApp"] = workerConfig.WorkerApp
	msgBody["workerGroup"] = workerConfig.WorkerGroup
	msgBody["groupTokenName"] = workerConfig.GroupTokenName
	msgBody["groupTokenSecret"] = workerConfig.GroupTokenSecret
	msgBody["appRunId"] = workerConfig.AppRunId
	msgBody["appStartTimestamp"] = workerConfig.AppStartTimestamp
	msgBody["workerName"] = workerConfig.WorkerName
	msgBody["maxActiveJobNum"] = 1

	body, _ := json.Marshal(msgBody)

	err := worker.stompConn.Send(destination, "application/json", body)

	log.Infof("%s send register req, err %s", logPrefix, err)

	if err != nil {
		return err
	}

	registerTimer := time.NewTimer(time.Duration(manager.registerTimeout) * time.Second)
	registerTimerCancel := make(chan struct{})
	manager.registerTimer = registerTimer
	manager.registerTimerCancel = registerTimerCancel
	go func() {
		cancelled := false
		select {
		case <-registerTimer.C:
		case <-registerTimerCancel:
			cancelled = true
		}
		eventRegisterTimeout := WorkerEvent{
			Type: eventRegisterTimeout,
			Data: &eventRegisterTimeoutData{
				timer:     registerTimer,
				cancelled: cancelled,
			},
		}
		worker.eventChan <- eventRegisterTimeout
	}()

	return nil
}
