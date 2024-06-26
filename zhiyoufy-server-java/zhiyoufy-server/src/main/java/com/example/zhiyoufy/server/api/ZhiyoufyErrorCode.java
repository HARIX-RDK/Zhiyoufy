package com.example.zhiyoufy.server.api;

import com.example.zhiyoufy.common.api.CustomDetailErrorCode;
import com.example.zhiyoufy.common.api.IErrorCode;

public enum ZhiyoufyErrorCode implements IErrorCode {
	RES_WORKER_GROUP_NOT_ACTIVE(5000, "RES_WORKER_GROUP_NOT_ACTIVE", "WorkerGroup不在线"),

	RES_INSUFFICIENT_WORKER_RESOURCE(5001, "RES_INSUFFICIENT_WORKER_RESOURCE", "Worker资源不足"),

	RES_WORKER_GROUP_ENV_NOT_MATCH(5001, "RES_WORKER_GROUP_ENV_NOT_MATCH", "WorkerGroup与Environment不匹配"),

	RES_MAX_ZHIYOUFY_ERROR_CODE(9999, "PlaceholderOnly", "");

	private final int code;
	private final String message;
	private final String detail;

	ZhiyoufyErrorCode(int code, String message, String detail) {
		this.code = code;
		this.message = message;
		this.detail = detail;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDetail() {
		return detail;
	}

	public CustomDetailErrorCode toCustomDetail(String customDetail) {
		return new CustomDetailErrorCode(code, message, customDetail);
	}
}
