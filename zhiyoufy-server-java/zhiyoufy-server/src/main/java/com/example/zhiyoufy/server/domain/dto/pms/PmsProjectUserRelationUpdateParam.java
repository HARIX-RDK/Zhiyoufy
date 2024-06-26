package com.example.zhiyoufy.server.domain.dto.pms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PmsProjectUserRelationUpdateParam {
	private Boolean isOwner;

	private Boolean isEditor;
}
