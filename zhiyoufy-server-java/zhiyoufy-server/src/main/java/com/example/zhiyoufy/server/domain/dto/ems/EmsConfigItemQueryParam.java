package com.example.zhiyoufy.server.domain.dto.ems;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmsConfigItemQueryParam {
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
