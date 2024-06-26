package com.example.zhiyoufy.server.domain.dto.ems;

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
public class EmsConfigItemParam {
	@NotNull
	private Long environmentId;
	@NotEmpty
	private String environmentName;

	@NotNull
	private Long collectionId;
	@NotEmpty
	private String collectionName;

	@NotEmpty
	@Size(min = 3)
	private String name;

	private String configValue;
	private String tags;
	private Integer sort;
	private Boolean disabled;
}
