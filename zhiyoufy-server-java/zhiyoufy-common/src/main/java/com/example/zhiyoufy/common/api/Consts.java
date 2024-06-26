package com.example.zhiyoufy.common.api;

public class Consts {
	/**
	 * 消息来源定义，有可能是外部的，也可能是内部的定时器之类的
	 */
	public static class ReqSrc {
		public static final String RestController = "RestController";
	}

	public static class HttpHeader {
		public static final String TimeoutMs = "timeout-ms";
	}
}
