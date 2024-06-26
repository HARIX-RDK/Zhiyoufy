package zhiyoufyworker

import (
	"fmt"

	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
)

type TaskStopJobRunner struct {
	ResultChan chan WorkerEvent
	JobRunner  *jms.JobRunner
}

const EventTaskStopJobRunnerEnd WorkerEventType = "EventTaskStopJobRunnerEnd"

func (task *TaskStopJobRunner) Run() {
	err := task.JobRunner.Stop()

	if err != nil {
		panic(fmt.Sprintf("TaskStopJobRunner met error %s", err))
	}

	event := WorkerEvent{
		Type: EventTaskStopJobRunnerEnd,
		Data: task,
	}
	task.ResultChan <- event
}
