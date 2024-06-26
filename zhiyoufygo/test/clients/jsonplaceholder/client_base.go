package jsonplaceholder

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/clients/jsonplaceholder"
	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
)

func NewClient(runContext core.IRunContext) *jsonplaceholder.Client {
	client := jsonplaceholder.NewClient(jsonplaceholder.ClientConfig{
		RunContext: runContext,
	})

	return client
}
