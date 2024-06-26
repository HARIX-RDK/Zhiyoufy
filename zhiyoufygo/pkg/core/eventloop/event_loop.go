package eventloop

import (
	"errors"
	"sync"
	"sync/atomic"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
)

var (
	ErrInvalidState = errors.New("invalid state")
)

type GeneralEventType string

const (
	EventStart GeneralEventType = "EventStart"
	EventStop  GeneralEventType = "EventStop"
)

type GeneralEvent struct {
	Type GeneralEventType
	Data any
}

type RunMainFunc func()

type EventLoopConfig struct {
	LogContext    core.ILogContext
	RunMainFunc   RunMainFunc
	EventChanSize int
}

type EventLoop struct {
	EventChan chan GeneralEvent
	Stopping  bool

	logContext  core.ILogContext
	running     atomic.Bool     // 是否调用Run
	runMutex    *sync.Mutex     // 用于同步Run, Stop
	runWg       *sync.WaitGroup // Stop时候用于同步等待
	runMainFunc RunMainFunc
}

func New(config *EventLoopConfig) *EventLoop {
	eventLoop := &EventLoop{
		logContext:  config.LogContext,
		runMutex:    &sync.Mutex{},
		runWg:       &sync.WaitGroup{},
		runMainFunc: config.RunMainFunc,
		EventChan:   make(chan GeneralEvent, config.EventChanSize),
	}
	return eventLoop
}

func (eventLoop *EventLoop) IsRunning() bool {
	return eventLoop.running.Load()
}

func (eventLoop *EventLoop) Run() error {
	logPrefix := "EventLoop.Run"

	if eventLoop.logContext.IsInfoEnabled() {
		log.Infof("%s %s Enter", eventLoop.logContext.GetTag(), logPrefix)
		defer log.Infof("%s %s Leave", eventLoop.logContext.GetTag(), logPrefix)
	}

	eventLoop.runMutex.Lock()
	defer eventLoop.runMutex.Unlock()

	if eventLoop.running.Load() {
		return ErrInvalidState
	}
	eventLoop.running.Store(true)
	eventLoop.runWg = &sync.WaitGroup{}
	eventLoop.runWg.Add(1)

	go eventLoop.doRun()
	eventLoop.SendEvent(EventStart, nil)

	return nil
}

func (eventLoop *EventLoop) Stop() error {
	logPrefix := "EventLoop.Stop"

	if eventLoop.logContext.IsInfoEnabled() {
		log.Infof("%s %s Enter", eventLoop.logContext.GetTag(), logPrefix)
		defer log.Infof("%s %s Leave", eventLoop.logContext.GetTag(), logPrefix)
	}

	eventLoop.runMutex.Lock()
	defer eventLoop.runMutex.Unlock()

	if !eventLoop.running.Load() {
		return ErrInvalidState
	}

	eventLoop.SendEvent(EventStop, nil)
	eventLoop.runWg.Wait()
	eventLoop.running.Store(false)
	eventLoop.Stopping = false

	return nil
}

func (eventLoop *EventLoop) SendEvent(eventType GeneralEventType, data any) {
	event := GeneralEvent{
		Type: eventType,
		Data: data,
	}
	eventLoop.EventChan <- event
}

func (eventLoop *EventLoop) doRun() {
	eventLoop.runMainFunc()

	eventLoop.runWg.Done()
}

// func (obj *SubClass) runMainFunc() {
// 	for event := range obj.EventLoop.EventChan {
// 		obj.processEvent(event)

// 		if obj.Stopping {
// 			// 等待goroutine结束，代表不会有内部新消息

// 			break
// 		}
// 	}
// }

// func (obj *SubClass) processEvent(event eventloop.GeneralEvent) {
// 	logPrefix := "SubClass.processEvent"

// 	log.Infof("%s Enter %s", logPrefix, event.Type)
// 	defer log.Infof("%s Leave %s", logPrefix, event.Type)

// 	switch event.Type {
// 	case eventloop.EventStart:
// 	case eventloop.EventStop:
// 	}
// }
