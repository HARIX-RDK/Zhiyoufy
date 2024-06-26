package api

import "fmt"

type ErrorInfo struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Detail  string `json:"detail"`
}

func (info ErrorInfo) Error() string {
	return fmt.Sprintf("code %d, message %s, detail %s", info.Code, info.Message, info.Detail)
}

func (info ErrorInfo) Clone() *ErrorInfo {
	cloned := ErrorInfo{
		Code:    info.Code,
		Message: info.Message,
		Detail:  info.Detail,
	}
	return &cloned
}

func (info *ErrorInfo) WithDetail(detail string) *ErrorInfo {
	info.Detail = detail
	return info
}

type BaseResponse struct {
	Err *ErrorInfo `json:"error"`
}
