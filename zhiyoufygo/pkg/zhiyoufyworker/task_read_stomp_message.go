package zhiyoufyworker

import (
	"github.com/go-stomp/stomp/v3"
)

type TaskReadStompMessage struct {
	ResultChan   chan WorkerEvent
	StompConn    *stomp.Conn
	Subscription *stomp.Subscription

	// output
	Err error
}

const (
	EventInStompMessage          WorkerEventType = "EventInStompMessage"
	EventTaskReadStompMessageEnd WorkerEventType = "EventTaskReadStompMessageEnd"
)

func (task *TaskReadStompMessage) Run() {
	defer func() {
		event := WorkerEvent{
			Type: EventTaskReadStompMessageEnd,
			Data: task,
		}
		task.ResultChan <- event
	}()

	for {
		msg, err := task.Subscription.Read()

		if err != nil {
			task.Err = err
			return
		}

		event := WorkerEvent{
			Type: EventInStompMessage,
			Data: msg,
		}
		task.ResultChan <- event
	}
}
