package com.example.zhiyoufy.server.domain.dto.wms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsWorkerGroupQueryParam {
	private Boolean exactMatch;
	private String keyword;
	private String sortBy;
	private Boolean sortDesc;
}
