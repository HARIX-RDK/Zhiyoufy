package com.example.zhiyoufy.server.domain.dto.pms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmsProjectQueryParam {
	private Boolean allUsers;
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
