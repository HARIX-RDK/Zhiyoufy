package com.example.zhiyoufy.common.elk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * 响应数据定义
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardRspData {
	private Integer statusCode;
}
