package com.example.zhiyoufy.server.domain.dto.pms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmsJobFolderQueryParam {
	private Long parentId;
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
