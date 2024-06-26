package zhiyoufyworker

import (
	"testing"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/zhiyoufyworker"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func TestWorkerRegisterOk(t *testing.T) {
	logPrefix := "TestWorkerRegisterOk"

	test.LoadConfig(t)

	worker := NewZhiyoufyWorker(t)
	registerStateUpdateChan := make(chan *zhiyoufyworker.EventRegisterStateUpdate)
	registerStateUpdateHandler := func(event *zhiyoufyworker.EventRegisterStateUpdate) {
		select {
		case registerStateUpdateChan <- event:
		default:
		}
	}
	worker.AddEventListener(registerStateUpdateHandler)

	waitRegisterOkTimer := time.NewTimer(5 * time.Second)

	err := worker.Run()

	require.True(t, err == nil)

	var eventRegisterStateUpdate *zhiyoufyworker.EventRegisterStateUpdate

	for {
		select {
		case <-waitRegisterOkTimer.C:
			t.Fatal("timeout not registered")
		case eventRegisterStateUpdate = <-registerStateUpdateChan:
		}

		log.Infof("%s registered %v", logPrefix, eventRegisterStateUpdate.Registered)

		if !eventRegisterStateUpdate.Registered {
			continue
		}

		break
	}

	// <-time.NewTimer(20 * time.Second).C

	err = worker.Stop()

	require.True(t, err == nil)
}
