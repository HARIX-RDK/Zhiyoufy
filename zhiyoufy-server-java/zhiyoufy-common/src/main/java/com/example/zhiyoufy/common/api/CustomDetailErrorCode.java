package com.example.zhiyoufy.common.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomDetailErrorCode implements IErrorCode {
	private final int code;
	private final String message;
	private final String detail;

	public CustomDetailErrorCode(int code, String message, String detail) {
		this.code = code;
		this.message = message;
		this.detail = detail;
	}
}
