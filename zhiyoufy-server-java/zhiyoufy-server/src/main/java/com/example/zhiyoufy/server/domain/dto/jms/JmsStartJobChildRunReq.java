package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsStartJobChildRunReq {
	private String runGuid;

	private long environmentId;
	private String environmentName;

	private long templateId;
	private String templateName;

	private int runNum;
	private int parallelNum;

	private int index;

	private String jobPath;
	private Integer timeoutSeconds;
	private String baseConfPath;
	private String privateConfPath;
	private String configComposite;
	private String extraArgs;
}
