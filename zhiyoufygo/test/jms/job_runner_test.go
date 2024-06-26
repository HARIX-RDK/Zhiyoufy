package jms

import (
	"testing"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/handlers/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func TestJobRunnerOk(t *testing.T) {
	logPrefix := "TestJobRunnerOk"

	test.LoadConfig(t)

	log.Infof("%s Enter", logPrefix)

	scriptRunnerCustomizer := func(runner *script.ScriptJobRunner) error {
		commandHandler := jsonplaceholder.NewCommandHandler()
		err := runner.RegisterCommandHandler(commandHandler)
		return err
	}

	content, err := test.TestdataFS.ReadFile("testdata/simpleForLoop.json")

	require.True(t, err == nil)

	jobRunnerConfig := jms.JobRunnerConfig{
		ParallelNum:            2000,
		SpawnIntervalMs:        100,
		SpawnNum:               100,
		ScriptContent:          content,
		ScriptRunnerCustomizer: scriptRunnerCustomizer,
		LogGroupSize:           100,
	}

	jobRunner := jms.NewJobRunner(jobRunnerConfig)

	doneChan := make(chan struct{})
	defer func() {
		select {
		case <-doneChan:
		default:
			close(doneChan)
		}
	}()

	jobFinishChan := make(chan *jms.EventJobFinishData)
	jobFinishHandler := func(data *jms.EventJobFinishData) {
		select {
		case jobFinishChan <- data:
		case <-doneChan:
		}
	}
	jobRunner.AddEventListener(jobFinishHandler)

	err = jobRunner.Run()

	require.True(t, err == nil)

	waitJobFinishTimer := time.NewTimer(7 * time.Second)

	select {
	case <-waitJobFinishTimer.C:
		t.Fatal("timeout not job finished")
	case <-jobFinishChan:
	}

	close(doneChan)
	err = jobRunner.Stop()

	require.True(t, err == nil)
}

func TestJobRunnerStopEarly(t *testing.T) {
	logPrefix := "TestJobRunnerStopEarly"

	test.LoadConfig(t)

	log.Infof("%s Enter", logPrefix)

	scriptRunnerCustomizer := func(runner *script.ScriptJobRunner) error {
		commandHandler := jsonplaceholder.NewCommandHandler()
		err := runner.RegisterCommandHandler(commandHandler)
		return err
	}

	content, err := test.TestdataFS.ReadFile("testdata/simpleForLoopWithSleep.json")

	require.True(t, err == nil)

	jobRunnerConfig := jms.JobRunnerConfig{
		ParallelNum:            2000,
		SpawnIntervalMs:        100,
		SpawnNum:               100,
		ScriptContent:          content,
		ScriptRunnerCustomizer: scriptRunnerCustomizer,
		LogGroupSize:           100,
	}

	jobRunner := jms.NewJobRunner(jobRunnerConfig)

	doneChan := make(chan struct{})
	defer func() {
		select {
		case <-doneChan:
		default:
			close(doneChan)
		}
	}()

	jobFinishChan := make(chan *jms.EventJobFinishData)
	jobFinishHandler := func(data *jms.EventJobFinishData) {
		select {
		case jobFinishChan <- data:
		case <-doneChan:
		}
	}
	jobRunner.AddEventListener(jobFinishHandler)

	err = jobRunner.Run()

	require.True(t, err == nil)

	<-time.NewTimer(5 * time.Second).C

	go jobRunner.Stop()

	waitJobFinishTimer := time.NewTimer(5 * time.Second)

	select {
	case <-waitJobFinishTimer.C:
		t.Fatal("timeout not job finished")
	case <-jobFinishChan:
	}
}
