package com.example.zhiyoufy.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * code用int目的方便按范围过滤，message为要展现给用户的消息，
 * detail为调试用，可以在前端打印在console之类的
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo implements IErrorCode {
	int code;
	String message;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	String detail;

	public static ErrorInfo of(IErrorCode errorCode) {
		return new ErrorInfo(errorCode.getCode(),
				errorCode.getMessage(), errorCode.getDetail());
	}

	public static ErrorInfo of(IErrorCode errorCode, String detail) {
		return new ErrorInfo(errorCode.getCode(),
				errorCode.getMessage(), detail);
	}
}
