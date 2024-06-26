package com.example.zhiyoufy.server.domain.dto.ems;

import javax.validation.constraints.NotEmpty;
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
public class EmsEnvironmentParam {
	private Long parentId;
	private String parentName;

	@NotEmpty
	@Size(min = 3)
	private String name;

	private String description;
	private String workerLabels;
	private String extraArgs;
}
