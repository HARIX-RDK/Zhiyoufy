package jms

type JmsEventType string

type JmsEvent struct {
	Type JmsEventType
	Data any
}
