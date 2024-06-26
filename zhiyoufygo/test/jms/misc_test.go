package jms

import (
	"path/filepath"
	"testing"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/jms"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func TestDownloadFile(t *testing.T) {
	logPrefix := t.Name()

	test.LoadConfig(t)

	log.Infof("%s Enter", logPrefix)

	localPath := filepath.Join(test.TestConfig.TestDataLocalDir, "multiple_configs_v20240305.json")

	cfgMap := map[string]any{
		"multiple_configs_file": map[string]any{
			"fileUrl":    "http://10.11.35.240:8080/docs/test_data/multiple_configs.json",
			"fileDigest": "60c6980b9a388c764d6f3e2fec634099",
			"localPath":  localPath,
		},
	}

	content, err := test.TestdataFS.ReadFile("testdata/downloadFile.json")

	require.True(t, err == nil)

	jobRunnerConfig := jms.JobRunnerConfig{
		ParallelNum:            100,
		SpawnIntervalMs:        100,
		SpawnNum:               100,
		ScriptContent:          content,
		ScriptRunnerCustomizer: nil,
		LogGroupSize:           10,
		CfgMap:                 cfgMap,
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
	case finishData := <-jobFinishChan:
		failedCnt := finishData.ChildStatusCntMap[script.Status_Failed]
		log.Infof("%s failedCnt %d", logPrefix, failedCnt)
		require.True(t, failedCnt == 0)
	}

	close(doneChan)
	err = jobRunner.Stop()

	require.True(t, err == nil)
}

func TestLoadFile(t *testing.T) {
	logPrefix := t.Name()

	test.LoadConfig(t)

	log.Infof("%s Enter", logPrefix)

	localPath := filepath.Join(test.TestConfig.TestDataDir, "multiple_configs_v20240305.json")

	cfgMap := map[string]any{
		"multiple_configs_file": map[string]any{
			"localPath": localPath,
		},
	}

	content, err := test.TestdataFS.ReadFile("testdata/loadFile.json")

	require.True(t, err == nil)

	jobRunnerConfig := jms.JobRunnerConfig{
		ParallelNum:            100,
		SpawnIntervalMs:        100,
		SpawnNum:               100,
		ScriptContent:          content,
		ScriptRunnerCustomizer: nil,
		LogGroupSize:           10,
		CfgMap:                 cfgMap,
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
	case finishData := <-jobFinishChan:
		failedCnt := finishData.ChildStatusCntMap[script.Status_Failed]
		log.Infof("%s failedCnt %d", logPrefix, failedCnt)
		require.True(t, failedCnt == 0)
	}

	close(doneChan)
	err = jobRunner.Stop()

	require.True(t, err == nil)
}

func TestSetVarFromCollection(t *testing.T) {
	logPrefix := t.Name()

	test.LoadConfig(t)

	log.Infof("%s Enter", logPrefix)

	localPath := filepath.Join(test.TestConfig.TestDataDir, "multiple_configs_v20240305.json")

	cfgMap := map[string]any{
		"multiple_configs_file": map[string]any{
			"localPath": localPath,
		},
		"multiple_configs_stride": 5,
	}

	content, err := test.TestdataFS.ReadFile("testdata/setVarFromCollection.json")

	require.True(t, err == nil)

	jobRunnerConfig := jms.JobRunnerConfig{
		RunnerIdx:              1,
		ParallelNum:            5,
		SpawnIntervalMs:        100,
		SpawnNum:               100,
		ScriptContent:          content,
		ScriptRunnerCustomizer: nil,
		LogGroupSize:           1,
		CfgMap:                 cfgMap,
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
	case finishData := <-jobFinishChan:
		failedCnt := finishData.ChildStatusCntMap[script.Status_Failed]
		log.Infof("%s failedCnt %d", logPrefix, failedCnt)
		require.True(t, failedCnt == 0)
	}

	close(doneChan)
	err = jobRunner.Stop()

	require.True(t, err == nil)
}
