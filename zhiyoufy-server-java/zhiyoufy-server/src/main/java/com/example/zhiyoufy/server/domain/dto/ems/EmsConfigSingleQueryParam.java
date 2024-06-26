package com.example.zhiyoufy.server.domain.dto.ems;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取config single列表参数
 */
@Getter
@Setter
public class EmsConfigSingleQueryParam {
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
