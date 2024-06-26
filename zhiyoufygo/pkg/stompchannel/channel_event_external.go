package stompchannel

import "github.com/go-stomp/stomp/v3"

var (
	EventReconnect ChannelEventType = "EventReconnect"
)

type EventStateUpdateData struct {
	NewState  ConnectState
	StompConn *stomp.Conn
}

type EventReconnectData struct {
	StompConn *stomp.Conn
}
