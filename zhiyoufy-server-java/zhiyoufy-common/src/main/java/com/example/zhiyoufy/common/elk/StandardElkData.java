package com.example.zhiyoufy.common.elk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * elk记录中stdData的通用定义
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardElkData {
	private StandardReqData req;
	private StandardRspData rsp;
}
