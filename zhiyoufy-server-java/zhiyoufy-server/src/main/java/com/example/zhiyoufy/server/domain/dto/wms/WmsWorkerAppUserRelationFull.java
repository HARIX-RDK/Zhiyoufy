package com.example.zhiyoufy.server.domain.dto.wms;

import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsWorkerAppUserRelationFull extends WmsWorkerAppUserRelation {
	private String username;
}
