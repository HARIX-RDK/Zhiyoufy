package com.example.zhiyoufy.server.domain.dto.ums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsRoleQueryParam {
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
