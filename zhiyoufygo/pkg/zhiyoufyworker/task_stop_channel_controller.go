package zhiyoufyworker

import (
	"fmt"

	"github.com/zhiyoufy/zhiyoufygo/pkg/stompchannel"
)

type TaskStopChannelController struct {
	ResultChan        chan WorkerEvent
	ChannelController *stompchannel.ChannelController
}

const EventTaskStopChannelControllerEnd WorkerEventType = "EventTaskStopChannelControllerEnd"

func (task *TaskStopChannelController) Run() {
	err := task.ChannelController.Stop()

	if err != nil {
		panic(fmt.Sprintf("TaskStopChannelController met error %s", err))
	}

	event := WorkerEvent{
		Type: EventTaskStopChannelControllerEnd,
		Data: task,
	}
	task.ResultChan <- event
}
