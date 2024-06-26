package com.example.zhiyoufy.server.domain.dto.jms;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName="job-child-run-result")
public class JmsJobChildRunResultFull {
	@Id
	private String id;

	@Field(type= FieldType.Date)
	Date timestamp;

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
	String messageId;

	@Field(type= FieldType.Keyword)
	String runGuid;

	Integer index;
	String endReason;
	String endDetail;

	Boolean endOk;
	Boolean resultOk;
	Boolean passed;

	@Field(type= FieldType.Keyword)
	String jobOutputUrl;

	Map<String, Integer> childStatusCntMap;
}
