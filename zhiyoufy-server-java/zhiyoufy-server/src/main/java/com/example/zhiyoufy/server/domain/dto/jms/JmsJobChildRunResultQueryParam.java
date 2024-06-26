package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsJobChildRunResultQueryParam {
	private Long environmentId;
	private long projectId;
	private Long templateId;
	private String runGuid;
}
