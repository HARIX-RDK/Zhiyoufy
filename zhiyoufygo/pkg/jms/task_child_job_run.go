package jms

import (
	"context"

	log "github.com/sirupsen/logrus"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
)

type taskChildJobRun struct {
	ResultChan chan JmsEvent

	runId  string
	runner *script.ScriptJobRunner
	runCtx *script.ScriptRunContext

	idx                  int
	rootCtx              context.Context
	rootCtxCancel        context.CancelFunc
	finallyRootCtx       context.Context
	finallyRootCtxCancel context.CancelFunc
}

func (task *taskChildJobRun) run() {
	logPrefix := "taskChildJobRun.run " + task.runId

	log.Infof("%s Enter", logPrefix)
	defer log.Infof("%s Leave", logPrefix)

	defer func() {
		event := JmsEvent{
			Type: eventTaskChildJobRunEnd,
			Data: task,
		}
		task.ResultChan <- event
	}()

	task.runner.RunScript(task.runCtx)
}
