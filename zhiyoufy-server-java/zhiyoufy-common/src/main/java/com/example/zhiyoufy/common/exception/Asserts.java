package com.example.zhiyoufy.common.exception;

import com.example.zhiyoufy.common.api.IErrorCode;

/**
 * 断言处理类，用于抛出各种API异常
 */
public class Asserts {
	public static void fail(String message) {
		throw new RuntimeException(message);
	}

	public static void fail(IErrorCode errorCode) {
		throw new ErrorCodeException(errorCode);
	}
}
