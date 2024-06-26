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
public class PmsFavoriteFolderParam {
	@NotNull
	private Long projectId;
	@NotEmpty
	private String projectName;

	@NotEmpty
	@Size(min = 3)
	private String name;
}
