package com.example.zhiyoufy.common.elk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * elk记录中data的通用定义
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
public class ElkData {
	private Object req;
	private Object rsp;
}
