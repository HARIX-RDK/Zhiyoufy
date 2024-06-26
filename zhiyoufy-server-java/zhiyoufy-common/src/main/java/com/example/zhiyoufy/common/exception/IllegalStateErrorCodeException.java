package com.example.zhiyoufy.common.exception;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.ErrorInfo;

public class IllegalStateErrorCodeException extends ErrorCodeException {
	public IllegalStateErrorCodeException(String reason) {
		super(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_STATE, reason));
	}

	public IllegalStateErrorCodeException(String reason, Throwable cause) {
		super(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_STATE, reason), cause);
	}
}
