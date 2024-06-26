package stompchannel

import "time"

var (
	eventConnectToServer  ChannelEventType = "eventConnectToServer"
	eventReconnectTimeout ChannelEventType = "eventReconnectTimeout"
	eventReadLoopEnd      ChannelEventType = "eventReadLoopEnd"
	eventStop             ChannelEventType = "eventStop"
)

type eventReconnectTimeoutData struct {
	timer     *time.Timer
	cancelled bool
}
