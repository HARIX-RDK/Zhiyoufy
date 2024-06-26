package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsJobRunResultQueryParam {
	private Long environmentId;
	private long projectId;
	private String templateName;
	private String runTag;
}
