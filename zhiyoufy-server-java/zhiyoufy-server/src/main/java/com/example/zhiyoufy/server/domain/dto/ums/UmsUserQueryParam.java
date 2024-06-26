package com.example.zhiyoufy.server.domain.dto.ums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsUserQueryParam {
	private Boolean admin;
	private Boolean sysAdmin;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
