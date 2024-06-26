package com.example.zhiyoufy.common.exception;

import com.example.zhiyoufy.common.api.IErrorCode;
import lombok.Getter;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

/**
 * 可定义errorCode的exception
 */
@Getter
public class ErrorCodeException extends ResponseStatusException {
	private final IErrorCode errorCode;

	public ErrorCodeException(IErrorCode errorCode) {
		this(errorCode, HttpStatus.OK, null);
	}

	public ErrorCodeException(IErrorCode errorCode, HttpStatus status) {
		this(errorCode, status, null);
	}

	public ErrorCodeException(IErrorCode errorCode, @Nullable Throwable cause) {
		this(errorCode, HttpStatus.OK, cause);
	}

	public ErrorCodeException(IErrorCode errorCode, HttpStatus status, @Nullable Throwable cause) {
		super(status, errorCode.getDetail(), cause);
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		StringBuilder stringBuilder = new StringBuilder(errorCode.getMessage());
		stringBuilder.append(", ");
		stringBuilder.append(super.getMessage());
		return stringBuilder.toString();
	}
}
