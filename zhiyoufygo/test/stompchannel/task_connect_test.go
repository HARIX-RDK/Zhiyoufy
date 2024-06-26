package stompchannel

import (
	"context"
	"testing"
	"time"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/pkg/stompchannel"
	"github.com/zhiyoufy/zhiyoufygo/test"
)

func TestTaskConnectOk(t *testing.T) {
	test.LoadConfig(t)

	resultChan := make(chan stompchannel.ChannelEvent)
	taskCtx, cancelCtx := context.WithCancel(context.Background())
	task := &stompchannel.TaskConnect{
		ServerAddr:   test.TestConfig.ZhiyoufyServerAddr,
		Subprotocols: subProtocols,
		Ctx:          taskCtx,
		ResultChan:   resultChan,
	}

	go task.Run()
	var event stompchannel.ChannelEvent
	ok := true

	select {
	case event = <-resultChan:
	case <-time.After(taskConnectTimeout):
		ok = false
	}
	cancelCtx()

	require.True(t, ok == true)

	resultTask, ok := event.Data.(*stompchannel.TaskConnect)

	require.True(t, ok == true)
	require.True(t, resultTask == task)
	require.True(t, task.Err == nil)

	task.WsConn.Close()
}

func TestTaskConnectFail(t *testing.T) {
	test.LoadConfig(t)

	resultChan := make(chan stompchannel.ChannelEvent)
	taskCtx, cancelCtx := context.WithCancel(context.Background())
	task := &stompchannel.TaskConnect{
		ServerAddr:   test.TestConfig.BadZhiyoufyServerAddr,
		Subprotocols: subProtocols,
		Ctx:          taskCtx,
		ResultChan:   resultChan,
	}

	go task.Run()
	var event stompchannel.ChannelEvent
	ok := true

	select {
	case event = <-resultChan:
	case <-time.After(taskConnectTimeout):
		ok = false
	}
	cancelCtx()

	require.True(t, ok == true)

	resultTask, ok := event.Data.(*stompchannel.TaskConnect)

	require.True(t, ok == true)
	require.True(t, resultTask == task)
	require.True(t, task.Err != nil)
}
