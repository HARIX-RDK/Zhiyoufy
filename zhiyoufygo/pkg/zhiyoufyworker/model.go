package zhiyoufyworker

import "github.com/zhiyoufy/zhiyoufygo/pkg/core/api"

var (
	RES_ERR_INVALID_STATE = api.ErrorInfo{
		Code:    1007,
		Message: "RES_ERR_INVALID_STATE",
	}
	RES_ERR_RESOURCE_NOT_AVAILABLE = api.ErrorInfo{
		Code:    1021,
		Message: "RES_ERR_RESOURCE_NOT_AVAILABLE",
	}
	RES_ERR_INVALID_CONFIG = api.ErrorInfo{
		Code:    1030,
		Message: "RES_ERR_INVALID_CONFIG",
	}
)
