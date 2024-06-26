package requestutil

import (
	"context"

	"github.com/zhiyoufy/zhiyoufygo/pkg/util/randutil"
)

type reqCtxKey string

var keyRequestIp reqCtxKey = "RequestIp"
var keyRequestId reqCtxKey = "RequestId"

func ContextGetRequestIp(ctx context.Context) string {
	requestIp := ctx.Value(keyRequestIp)

	if requestIp == nil {
		return ""
	} else {
		return requestIp.(string)
	}
}

func ContextGetRequestId(ctx context.Context) string {
	requestId := ctx.Value(keyRequestId)

	if requestId == nil {
		return randutil.GenerateShortHexId()
	} else {
		return requestId.(string)
	}
}

func ContextPutRequestId(ctx context.Context, requestId string) context.Context {
	return context.WithValue(ctx, keyRequestId, requestId)
}

func ContextWithRequestId(ctx context.Context) context.Context {
	if ctx.Value(keyRequestId) == nil {
		requestId := randutil.GenerateShortHexId()

		return context.WithValue(ctx, keyRequestId, requestId)
	}

	return ctx
}
