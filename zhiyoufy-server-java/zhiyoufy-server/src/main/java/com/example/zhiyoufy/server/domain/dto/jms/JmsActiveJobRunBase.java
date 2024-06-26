package com.example.zhiyoufy.server.domain.dto.jms;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsActiveJobRunBase {
	private String runGuid;

	private long workerAppId;
	private String workerAppName;

	private long workerGroupId;
	private String workerGroupName;

	private long environmentId;
	private String environmentName;

	private long projectId;
	private String projectName;

	private long templateId;
	private String templateName;

	private String runTag;
	private int runNum;
	private int parallelNum;

	private String username;
	private int startedNum;
	private int activeNum;
	private int finishedNum;
	private int passedNum;
	private Date createdOn;
	private String state;

	private boolean perf;
	private String dashboardAddr;
	private int perfParallelNum;
	private List<String> workerNames;
}
