package com.example.zhiyoufy.server.domain.dto.wms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WmsWorkerAppUserRelationUpdateParam {
	private Boolean isOwner;

	private Boolean isEditor;
}
