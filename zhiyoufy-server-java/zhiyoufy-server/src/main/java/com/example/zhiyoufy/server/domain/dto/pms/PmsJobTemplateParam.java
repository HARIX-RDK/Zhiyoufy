package com.example.zhiyoufy.server.domain.dto.pms;

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
public class PmsJobTemplateParam {
	@NotNull
	private Long projectId;
	@NotEmpty
	private String projectName;

	@NotNull
	private Long folderId;
	@NotEmpty
	private String folderName;

	@NotEmpty
	@Size(min = 3)
	private String name;

	private String description;

	@NotNull
	private String jobPath;

	private String workerLabels;

	@NotNull
	private Integer timeoutSeconds;

	private String baseConfPath;

	private String privateConfPath;

	private String configSingles;
	private String configCollections;

	private String extraArgs;

	private Boolean isPerf;
	private String dashboardAddr;
}
