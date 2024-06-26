package com.example.zhiyoufy.common.api;

/**
 * 错误码定义
 * number between 1 and 4999 is for error code agreed by different modules
 * errors seen by end user should fall into this range
 * number between 5000 and 9999 is for module specific error code
 */
public enum CommonErrorCode implements IErrorCode {
	RES_FAILED(500, "RES_FAILED", "操作失败"),

	RES_UNAUTHORIZED(401, "RES_UNAUTHORIZED", "鉴权失败"),
	RES_FORBIDDEN(403, "RES_FORBIDDEN", "没有相关权限"),
	RES_NOT_FOUND(404, "RES_NOT_FOUND", "资源不存在"),
	RES_TIMEOUT(408, "RES_TIMEOUT", "处理超时"),

	RES_VALIDATE_FAILED(1000, "RES_VALIDATE_FAILED", "参数检验失败"),
	RES_ILLEGAL_STATE(1001, "RES_ILLEGAL_STATE", "状态检验失败"),
	RES_ILLEGAL_ARGUMENT(1002, "RES_ILLEGAL_ARGUMENT", "参数检验失败"),
	RES_NAME_ALREADY_EXIST(1003, "RES_NAME_ALREADY_EXIST", "名字已存在"),
	RES_NON_CONFORMING_PASSWORD(1004, "RES_NON_CONFORMING_PASSWORD",
			"密码不合格，应长度大于等于6并同时含有大写字母，小写字母，数字和标点符号"),
	RES_SEND_MAIL_FAILED(1005, "RES_SEND_MAIL_FAILED", "发送邮件失败"),
	RES_OVER_CAPACITY(1006, "RES_OVER_CAPACITY", "超过容量上限"),
	RES_TOO_FREQUENT(1007, "RES_TOO_FREQUENT", "调用太频繁，等候重试"),
	RES_EMAIL_IN_USE(1008, "RES_EMAIL_IN_USE", "邮箱已使用"),
	RES_DELETE_LAST_OWNER_NOT_ALLOWED(1009, "RES_DELETE_LAST_OWNER_NOT_ALLOWED",
			"不允许删除最后一个拥有者"),

	RES_MAX_COMMON_ERROR_CODE(4999, "PlaceholderOnly", "");

	private final int code;
	private final String message;
	private final String detail;

	CommonErrorCode(int code, String message, String detail) {
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
