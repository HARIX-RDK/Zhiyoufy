package com.example.zhiyoufy.server.domain.dto.wms;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WmsWorkerRegisterParam {
	private String workerApp;
	private String workerGroup;
	private String groupTokenName;
	private String groupTokenSecret;

	private String appRunId;
	private Date appStartTimestamp;

	private String workerName;
	private Integer maxActiveJobNum;
}
