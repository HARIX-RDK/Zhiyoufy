package com.example.zhiyoufy.common.elk;

public class ElkConsts {
	/**
	 * 写log时候的特定标识符，通过filebeat提取涉及elk的log信息时会按照这个过滤
	 */
	public static final String JSON_EXTRACT_SUFFIX = "json_extract_prefix";
}
