package com.example.zhiyoufy.server.domain.dto.wms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WmsActiveJobBase {
	private String runGuid;
	private Integer idx;

	private Long templateId;
	private Long environmentId;
	private Integer parallelNum;

	public String getJobKey() {
		return "run-" + runGuid + "-idx-" + idx;
	}
}
