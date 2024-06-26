package com.example.zhiyoufy.common.exception;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.ErrorInfo;

public class IllegalArgumentErrorCodeException extends ErrorCodeException {
	public IllegalArgumentErrorCodeException() {
		super(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
	}

	public IllegalArgumentErrorCodeException(String reason) {
		super(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT, reason));
	}

	public IllegalArgumentErrorCodeException(Throwable cause) {
		super(CommonErrorCode.RES_ILLEGAL_ARGUMENT, cause);
	}

	public IllegalArgumentErrorCodeException(String reason, Throwable cause) {
		super(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT, reason), cause);
	}
}
