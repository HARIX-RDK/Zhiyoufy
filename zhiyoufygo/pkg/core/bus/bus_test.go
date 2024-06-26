package bus

import (
	"testing"

	"github.com/stretchr/testify/require"
)

type testQuery struct {
	ID   int64
	Resp string
}

func TestEventPublish(t *testing.T) {
	bus := New()

	var invoked bool

	bus.AddEventListener(func(query *testQuery) {
		invoked = true
	})

	bus.Publish(&testQuery{})

	require.True(t, invoked)
}

func TestEventPublish_NoRegisteredListener(t *testing.T) {
	bus := New()

	bus.Publish(&testQuery{})
}
