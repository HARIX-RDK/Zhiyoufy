package stompchannel

type ChannelEventType string

type ChannelEvent struct {
	Type ChannelEventType
	Data any
}
