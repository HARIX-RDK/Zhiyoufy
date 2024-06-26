package stompchannel

import (
	"context"
	"net/http"
	"time"

	"github.com/go-stomp/stomp/v3"
	"github.com/gorilla/websocket"
)

type TaskConnect struct {
	ServerAddr   string
	Subprotocols []string
	Ctx          context.Context
	ResultChan   chan ChannelEvent

	// output
	Err       error
	WsConn    *websocket.Conn
	StompConn *stomp.Conn
}

const EventTaskConnectEnd ChannelEventType = "EventTaskConnectEnd"

func (task *TaskConnect) Run() {
	dialer := &websocket.Dialer{
		Proxy:            http.ProxyFromEnvironment,
		HandshakeTimeout: 45 * time.Second,
		Subprotocols:     task.Subprotocols,
	}
	conn, _, err := dialer.DialContext(task.Ctx, task.ServerAddr, nil)

	if err != nil {
		task.Err = err
		event := ChannelEvent{
			Type: EventTaskConnectEnd,
			Data: task,
		}
		select {
		case task.ResultChan <- event:
		case <-task.Ctx.Done():
		}
		return
	}

	wsConn := &webSocketConn{conn: conn, traceOn: true}
	heartbeatDuration := 20 * time.Second

	stompConn, err := stomp.Connect(wsConn, stomp.ConnOpt.Logger(stompLogger{}),
		stomp.ConnOpt.HeartBeat(heartbeatDuration, heartbeatDuration),
		stomp.ConnOpt.HeartBeatError(heartbeatDuration))

	if err != nil {
		task.Err = err
		conn.Close()
		event := ChannelEvent{
			Type: EventTaskConnectEnd,
			Data: task,
		}
		select {
		case task.ResultChan <- event:
		case <-task.Ctx.Done():
		}
		return
	}

	task.WsConn = conn
	task.StompConn = stompConn
	event := ChannelEvent{
		Type: EventTaskConnectEnd,
		Data: task,
	}
	select {
	case task.ResultChan <- event:
	case <-task.Ctx.Done():
		conn.Close()
	}
}
