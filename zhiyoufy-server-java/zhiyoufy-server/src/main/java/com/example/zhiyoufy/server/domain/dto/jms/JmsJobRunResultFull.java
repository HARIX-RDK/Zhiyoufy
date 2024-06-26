package com.example.zhiyoufy.server.domain.dto.jms;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName="job-run-result")
public class JmsJobRunResultFull {
	@Id
	@Field(type= FieldType.Keyword)
	String runGuid;

	private long workerAppId;

	@Field(type= FieldType.Keyword)
	private String workerAppName;

	private long workerGroupId;

	@Field(type= FieldType.Keyword)
	private String workerGroupName;

	private long environmentId;

	@Field(type= FieldType.Keyword)
	private String environmentName;

	private long projectId;

	@Field(type= FieldType.Keyword)
	private String projectName;

	private long templateId;

	@Field(type= FieldType.Keyword)
	private String templateName;

	@Field(type= FieldType.Keyword)
	private String runTag;

	private int runNum;
	private int parallelNum;

	@Field(type= FieldType.Keyword)
	private String username;
	private int startedNum;
	private int finishedNum;
	private int passedNum;

	@Field(type= FieldType.Date)
	private Date createdOn;

	@Field(type= FieldType.Date)
	Date timestamp;

	private long durationSeconds;

	Boolean passed;
	String endReason;
}
