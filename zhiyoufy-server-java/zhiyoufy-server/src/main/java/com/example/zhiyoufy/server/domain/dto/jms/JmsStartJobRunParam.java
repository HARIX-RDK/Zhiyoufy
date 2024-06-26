package com.example.zhiyoufy.server.domain.dto.jms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JmsStartJobRunParam {
	@NotEmpty
	@Size(min = 16)
	private String runGuid;

	@Min(1)
	private long workerAppId;
	private String workerAppName;

	@Min(1)
	private long workerGroupId;
	private String workerGroupName;

	@Min(1)
	private long environmentId;
	private String environmentName;

	@Min(1)
	private long projectId;
	private String projectName;

	@Min(1)
	private long templateId;
	private String templateName;

	@NotEmpty
	private String runTag;

	@Min(1)
	private int runNum;
	@Min(1)
	private int parallelNum;

	private String includeTags;
	private String excludeTags;
	private String addTags;
	private String removeTags;

	private String username;
}
