package com.example.zhiyoufy.server.domain.dto.ems;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取environment列表参数
 *
 * allUsers:	全体用户的，需要admin权限
 */
@Getter
@Setter
public class EmsEnvironmentQueryParam {
	private Boolean allUsers;
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
