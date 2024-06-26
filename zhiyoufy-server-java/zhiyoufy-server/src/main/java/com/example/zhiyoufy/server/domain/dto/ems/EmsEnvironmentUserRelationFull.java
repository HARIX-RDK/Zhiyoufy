package com.example.zhiyoufy.server.domain.dto.ems;

import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmsEnvironmentUserRelationFull extends EmsEnvironmentUserRelation {
	private String username;
}
