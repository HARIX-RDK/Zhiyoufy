package com.example.zhiyoufy.server.domain.dto.ems;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmsConfigItemQueryParamForJob {
	private Integer runNum;
	private String includeTags;
	private String excludeTags;
}
