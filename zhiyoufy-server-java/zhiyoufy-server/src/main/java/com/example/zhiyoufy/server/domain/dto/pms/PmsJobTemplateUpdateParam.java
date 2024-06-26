package com.example.zhiyoufy.server.domain.dto.pms;

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
public class PmsJobTemplateUpdateParam {
	private String name;

	private String description;

	private String jobPath;

	private String workerLabels;

	private Integer timeoutSeconds;

	private String baseConfPath;

	private String privateConfPath;

	private String configSingles;
	private String configCollections;

	private String extraArgs;

	private Boolean isPerf;
	private String dashboardAddr;
}
