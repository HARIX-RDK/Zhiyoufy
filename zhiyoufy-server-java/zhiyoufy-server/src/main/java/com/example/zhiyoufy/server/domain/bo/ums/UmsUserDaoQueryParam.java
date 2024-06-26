package com.example.zhiyoufy.server.domain.bo.ums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsUserDaoQueryParam {
	private Boolean admin;
	private Boolean sysAdmin;
	private String keyword;
	private String orderByClause;
}
