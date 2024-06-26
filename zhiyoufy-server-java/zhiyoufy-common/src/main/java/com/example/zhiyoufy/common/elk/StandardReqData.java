package com.example.zhiyoufy.common.elk;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求相关数据定义，有的数据只在特定情况有，比如uri, queryString可能
 * 只在http请求时候有，序列化json时候这里配置了只写入非null的
 * 标准化定义，同样字段在不同应用写入时代表同样意思
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardReqData {
	private OffsetDateTime recvTimestamp;
	private String reqSrc;
	private String userName;
	private String userIp;
	private String uri;
	private String queryString;
	private Long timeoutMs;
}
