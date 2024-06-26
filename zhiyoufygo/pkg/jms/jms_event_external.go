package jms

import "github.com/zhiyoufy/zhiyoufygo/pkg/script"

const (
	EventJobFinish         JmsEventType = "EventJobFinish"
	EventUpdateParallelNum JmsEventType = "EventUpdateParallelNum"
)

type EventJobFinishData struct {
	Err               error
	ChildStatusCntMap map[script.ScriptJobStatus]int
}

type EventUpdateParallelNumData struct {
	ParallelNum int
}
