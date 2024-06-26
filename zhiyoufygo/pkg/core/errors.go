package core

import (
	"github.com/zhiyoufy/zhiyoufygo/pkg/core/api"
)

const (
	ErrCode_InvalidResponse = 1000
	ErrCode_InvalidState    = 1001
	ErrCode_InvalidParam    = 1002
)

var (
	ErrInvalidParam = api.ErrorInfo{
		Code:    ErrCode_InvalidParam,
		Message: "invalid param",
	}
	ErrInvalidResponse = api.ErrorInfo{
		Code:    ErrCode_InvalidResponse,
		Message: "invalid response",
	}
	ErrInvalidState = api.ErrorInfo{
		Code:    ErrCode_InvalidState,
		Message: "invalid state",
	}
)
