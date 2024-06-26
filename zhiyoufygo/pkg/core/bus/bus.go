package bus

import (
	"errors"
	"fmt"
	"reflect"
)

// HandlerFunc defines a handler function interface.
type HandlerFunc interface{}

// ErrHandlerNotFound defines an error if a handler is not found.
var ErrHandlerNotFound = errors.New("handler not found")

// Bus type defines the bus interface structure.
type Bus interface {
	Publish(msg any)
	AddEventListener(handler HandlerFunc)
}

// InProcBus defines the bus structure.
type InProcBus struct {
	listeners map[string][]HandlerFunc
}

func New() *InProcBus {
	return &InProcBus{
		listeners: make(map[string][]HandlerFunc),
	}
}

// Publish function publish a message to the bus listener.
func (b *InProcBus) Publish(msg any) {
	var msgName = reflect.TypeOf(msg).Elem().Name()

	if listeners, exists := b.listeners[msgName]; exists {
		params := []reflect.Value{reflect.ValueOf(msg)}
		callListeners(listeners, params)
	}
}

func callListeners(listeners []HandlerFunc, params []reflect.Value) {
	for _, listenerHandler := range listeners {
		reflect.ValueOf(listenerHandler).Call(params)
	}
}

func (b *InProcBus) AddEventListener(handler HandlerFunc) {
	handlerType := reflect.TypeOf(handler)

	if handlerType.NumIn() != 1 {
		panic(fmt.Errorf("handler has %d in parameters, expected 1", handlerType.NumIn()))
	}

	eventName := handlerType.In(0).Elem().Name()
	_, exists := b.listeners[eventName]
	if !exists {
		b.listeners[eventName] = make([]HandlerFunc, 0)
	}
	b.listeners[eventName] = append(b.listeners[eventName], handler)
}
