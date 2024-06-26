package stompchannel

import (
	"testing"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/stompchannel"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func TestChannelConnectOk(t *testing.T) {
	logPrefix := "TestChannelConnectOk"

	test.LoadConfig(t)

	controller := NewChannelController()

	doneChan := make(chan struct{})
	defer func() {
		select {
		case <-doneChan:
		default:
			close(doneChan)
		}
	}()

	stateUpdateChan := make(chan *stompchannel.EventStateUpdateData)
	stateUpdateHandler := func(data *stompchannel.EventStateUpdateData) {
		select {
		case stateUpdateChan <- data:
		case <-doneChan:
		}
	}
	controller.AddEventListener(stateUpdateHandler)

	waitConnectedTimer := time.NewTimer(5 * time.Second)

	err := controller.Run()

	require.True(t, err == nil)

	var data *stompchannel.EventStateUpdateData

	for {
		select {
		case <-waitConnectedTimer.C:
			t.Fatal("timeout not connected")
		case data = <-stateUpdateChan:
		}

		log.Infof("%s newState %s", logPrefix, data.NewState)

		if data.NewState != stompchannel.Connected {
			continue
		}
		break
	}

	close(doneChan)
	err = controller.Stop()

	require.True(t, err == nil)
}

func TestChannelReconnectOk(t *testing.T) {
	logPrefix := "TestChannelReconnectOk"

	test.LoadConfig(t)

	controller := NewChannelController()

	doneChan := make(chan struct{})
	defer func() {
		select {
		case <-doneChan:
		default:
			close(doneChan)
		}
	}()

	stateUpdateChan := make(chan *stompchannel.EventStateUpdateData)
	stateUpdateHandler := func(data *stompchannel.EventStateUpdateData) {
		select {
		case stateUpdateChan <- data:
		case <-doneChan:
		}
	}
	controller.AddEventListener(stateUpdateHandler)

	waitConnectedTimer := time.NewTimer(5 * time.Second)

	err := controller.Run()

	require.True(t, err == nil)

	var data *stompchannel.EventStateUpdateData

	for {
		select {
		case <-waitConnectedTimer.C:
			t.Fatal("timeout not connected")
		case data = <-stateUpdateChan:
		}

		log.Infof("%s newState %s", logPrefix, data.NewState)

		if data.NewState != stompchannel.Connected {
			continue
		}
		break
	}

	reconnectData := &stompchannel.EventReconnectData{StompConn: data.StompConn}
	controller.SendEvent(stompchannel.EventReconnect, reconnectData)

	waitConnectedTimer.Stop()
	waitConnectedTimer = time.NewTimer(5 * time.Second)
	metStateConnecting := false

	for {
		select {
		case <-waitConnectedTimer.C:
			t.Fatal("timeout not connected")
		case data = <-stateUpdateChan:
		}

		log.Infof("%s newState %s", logPrefix, data.NewState)

		if !metStateConnecting {
			if data.NewState == stompchannel.Connecting {
				metStateConnecting = true
			}
			continue
		}

		if data.NewState != stompchannel.Connected {
			continue
		}
		break
	}

	close(doneChan)
	err = controller.Stop()

	require.True(t, err == nil)
}

func TestChannelConnectFail(t *testing.T) {
	// 因为重试比较费时，所以常规Skip，只调试时候打开
	// t.SkipNow()

	logPrefix := "TestChannelConnectFail"

	test.LoadConfig(t)

	config := stompchannel.ChannelControllerConfig{
		ServerAddr: test.TestConfig.BadZhiyoufyServerAddr,
	}
	controller := stompchannel.NewChannelController(config)

	doneChan := make(chan struct{})
	defer func() {
		select {
		case <-doneChan:
		default:
			close(doneChan)
		}
	}()

	stateUpdateChan := make(chan *stompchannel.EventStateUpdateData)
	stateUpdateHandler := func(data *stompchannel.EventStateUpdateData) {
		select {
		case stateUpdateChan <- data:
		case <-doneChan:
		}
	}
	controller.AddEventListener(stateUpdateHandler)

	waitTimeoutTimer := time.NewTimer(20 * time.Second)

	err := controller.Run()

	require.True(t, err == nil)

	var data *stompchannel.EventStateUpdateData
	timeout := false

	for {
		select {
		case <-waitTimeoutTimer.C:
			timeout = true
		case data = <-stateUpdateChan:
		}

		if timeout {
			break
		}

		log.Infof("%s newState %s", logPrefix, data.NewState)

		if data.NewState != stompchannel.Connected {
			continue
		}
		break
	}

	require.True(t, timeout == true)

	close(doneChan)
	err = controller.Stop()

	require.True(t, err == nil)
}
