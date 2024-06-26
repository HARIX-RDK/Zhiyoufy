package stompchannel

import (
	"context"
	"errors"
	"fmt"
	"sync"
	"sync/atomic"
	"time"

	"github.com/go-stomp/stomp/v3"
	"github.com/gorilla/websocket"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core/backoff"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/bus"
)

var (
	ErrInvalidState = errors.New("invalid state")
	wsSubProtocols  = []string{"v12.stomp"}
)

type ChannelControllerConfig struct {
	EventBus   bus.Bus
	ServerAddr string
}

type ChannelController struct {
	bus.Bus

	serverAddr    string
	eventChan     chan ChannelEvent
	rootCtx       context.Context
	rootCtxCancel context.CancelFunc // 用于Stop时候cancel进行中工作
	runWg         *sync.WaitGroup    // Stop时候用于同步等待
	logPrefix     string

	// 状态相关
	activeReadLoop bool         // goroutine readLoop是否active
	connState      ConnectState // 大的状态
	running        atomic.Bool  // 是否调用Run
	taskConnect    *TaskConnect // 进行中的TaskConnect

	stompConn    *stomp.Conn
	subscription *stomp.Subscription
	wsConn       *websocket.Conn

	// 重连相关
	connectTrialCnt      int
	exponentialBackoff   *backoff.ExponentialBackoff
	reconnectTimer       *time.Timer
	reconnectTimerCancel chan struct{}
}

func NewChannelController(config ChannelControllerConfig) *ChannelController {
	controller := ChannelController{
		Bus:        config.EventBus,
		serverAddr: config.ServerAddr,
		connState:  Disconnected,
		logPrefix:  "ChannelController",
	}
	if controller.Bus == nil {
		controller.Bus = bus.New()
	}
	controller.exponentialBackoff = backoff.NewExponentialBackoff(nil)

	return &controller
}

func (controller *ChannelController) Run() error {
	logPrefix := controller.logPrefix + ".Run"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	if controller.running.Load() {
		return ErrInvalidState
	}
	controller.running.Store(true)
	controller.runWg = &sync.WaitGroup{}
	controller.runWg.Add(1)
	controller.eventChan = make(chan ChannelEvent, 10)

	ctx, cancel := context.WithCancel(context.Background())
	controller.rootCtx = ctx
	controller.rootCtxCancel = cancel

	go controller.doRun()
	controller.SendEvent(eventConnectToServer, nil)

	return nil
}

func (controller *ChannelController) Stop() error {
	logPrefix := controller.logPrefix + ".Stop"

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	if !controller.running.Load() {
		return ErrInvalidState
	}

	controller.SendEvent(eventStop, nil)
	controller.runWg.Wait()
	controller.running.Store(false)

	return nil
}

func (controller *ChannelController) SendEvent(eventType ChannelEventType, data any) {
	event := ChannelEvent{
		Type: eventType,
		Data: data,
	}
	controller.eventChan <- event
}

func (controller *ChannelController) doRun() {
	for event := range controller.eventChan {
		controller.processEvent(event)

		if controller.connState == Disconnecting {
			// 等待goroutine结束
			if controller.activeReadLoop || controller.taskConnect != nil ||
				controller.reconnectTimer != nil {
				continue
			}

			controller.setState(Disconnected)
			break
		}
	}

	controller.runWg.Done()
}

func (controller *ChannelController) setState(newState ConnectState) {
	logPrefix := controller.logPrefix + ".setState"

	if controller.connState != newState {
		log.Infof("%s state from %s to %s", logPrefix, controller.connState, newState)

		controller.connState = newState

		if controller.connState == Connecting {
			controller.onEnterStateConnecting()
		}

		eventData := &EventStateUpdateData{NewState: controller.connState}
		if controller.connState == Connected {
			eventData.StompConn = controller.stompConn
		}
		controller.Publish(eventData)
	}
}

func (controller *ChannelController) onEnterStateConnecting() {
	controller.connectTrialCnt = 0
	controller.exponentialBackoff.Reset()
}

func (controller *ChannelController) processEvent(event ChannelEvent) {
	logPrefix := controller.logPrefix + ".processEvent"

	log.Infof("%s Enter %s", logPrefix, event.Type)
	defer log.Infof("%s Leave %s", logPrefix, event.Type)

	switch event.Type {
	case eventReadLoopEnd:
		controller.onEventReadLoopEnd()
	case EventReconnect:
		controller.onEventReconnect(event)
	case eventReconnectTimeout:
		controller.onEventReconnectTimeout(event)
	case eventStop:
		controller.onEventStop()
	case EventTaskConnectEnd:
		controller.onEventTaskConnectEnd(event)
	case eventConnectToServer:
		controller.onEventConnectToServer()
	}
}

func (controller *ChannelController) onEventReadLoopEnd() {
	controller.subscription = nil
	controller.activeReadLoop = false

	if controller.connState == Connected {
		if controller.stompConn != nil {
			go controller.stompConn.MustDisconnect()
			controller.wsConn = nil
			controller.stompConn = nil
		}

		controller.setState(Connecting)

		controller.scheduleReconnect()
	}
}

func (controller *ChannelController) onEventReconnect(event ChannelEvent) {
	if controller.connState == Connected {
		data := event.Data.(*EventReconnectData)
		if data.StompConn == controller.stompConn {
			go controller.stompConn.MustDisconnect()
			controller.wsConn = nil
			controller.stompConn = nil
		}
	}
}

func (controller *ChannelController) onEventReconnectTimeout(event ChannelEvent) {
	controller.reconnectTimer = nil
	controller.reconnectTimerCancel = nil

	if controller.connState == Connecting {
		controller.doTaskConnect()
	}
}

func (controller *ChannelController) onEventStop() {
	controller.setState(Disconnecting)
	controller.rootCtxCancel()

	if controller.wsConn != nil {
		controller.wsConn.Close()
		controller.wsConn = nil
		controller.stompConn = nil
	}

	if controller.reconnectTimerCancel != nil {
		close(controller.reconnectTimerCancel)
		controller.reconnectTimerCancel = nil
	}
}

func (controller *ChannelController) onEventTaskConnectEnd(event ChannelEvent) {
	data := event.Data.(*TaskConnect)

	logPrefix := controller.logPrefix + ".onEventTaskConnectEnd"

	if data != controller.taskConnect {
		log.Infof("%s ignore old taskConnect", logPrefix)

		if data.WsConn != nil {
			data.WsConn.Close()
		}

		return
	}

	if data.Err != nil {
		log.Infof("%s Err %s", logPrefix, data.Err)
	}

	controller.taskConnect = nil

	if controller.connState == Disconnecting {
		if data.WsConn != nil {
			data.WsConn.Close()
		}
		return
	}

	if data.Err != nil {
		// 连接失败，等待一段时间重试
		controller.scheduleReconnect()
		return
	}

	subscription, err := data.StompConn.Subscribe(
		"", stomp.AckAuto, stomp.SubscribeOpt.Header(stomp.ReplyToHeader, "detect-conn-lost"))

	if err != nil {
		data.WsConn.Close()
		controller.scheduleReconnect()
		return
	}

	controller.wsConn = data.WsConn
	controller.stompConn = data.StompConn
	controller.setState(Connected)

	controller.subscription = subscription
	controller.activeReadLoop = true
	go controller.readLoop()
}

func (controller *ChannelController) onEventConnectToServer() {
	if controller.connState != Disconnected {
		panic(fmt.Sprintf("onEventConnectToServer: invalid state, state is %s, not Disconnected",
			controller.connState))
	}

	controller.setState(Connecting)

	controller.doTaskConnect()
}

func (controller *ChannelController) doTaskConnect() {
	logPrefix := controller.logPrefix + ".doTaskConnect"

	log.Infof("%s Enter", logPrefix)

	taskConnect := &TaskConnect{
		ServerAddr:   controller.serverAddr,
		Subprotocols: wsSubProtocols,
		Ctx:          controller.rootCtx,
		ResultChan:   controller.eventChan,
	}

	controller.taskConnect = taskConnect

	go taskConnect.Run()
}

func (controller *ChannelController) readLoop() {
	defer controller.SendEvent(eventReadLoopEnd, nil)

	for {
		_, err := controller.subscription.Read()

		if err != nil {
			return
		}
	}
}

func (controller *ChannelController) scheduleReconnect() {
	logPrefix := controller.logPrefix + ".scheduleReconnect"

	connectInterval := controller.exponentialBackoff.Next()

	log.Infof("%s connectTrialCnt %d, reconnect after %d",
		logPrefix, controller.connectTrialCnt, connectInterval)

	reconnectTimer := time.NewTimer(time.Duration(connectInterval) * time.Second)
	reconnectTimerCancel := make(chan struct{})
	controller.reconnectTimer = reconnectTimer
	controller.reconnectTimerCancel = reconnectTimerCancel
	go func() {
		cancelled := false
		select {
		case <-reconnectTimer.C:
		case <-reconnectTimerCancel:
			cancelled = true
		}
		eventReconnectTimeout := ChannelEvent{
			Type: eventReconnectTimeout,
			Data: &eventReconnectTimeoutData{
				timer:     reconnectTimer,
				cancelled: cancelled,
			},
		}
		controller.eventChan <- eventReconnectTimeout
	}()
}
