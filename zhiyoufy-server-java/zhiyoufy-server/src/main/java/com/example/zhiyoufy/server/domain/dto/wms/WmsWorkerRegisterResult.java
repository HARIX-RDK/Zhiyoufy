package com.example.zhiyoufy.server.domain.dto.wms;

import com.example.zhiyoufy.common.api.ErrorInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WmsWorkerRegisterResult {
	private ErrorInfo error;
	private String serverRunId;
}
