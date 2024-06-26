package com.example.zhiyoufy.server.domain.dto.pms;

import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmsProjectUserRelationFull extends PmsProjectUserRelation {
	private String username;
}
