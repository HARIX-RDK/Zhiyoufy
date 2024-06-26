package com.example.zhiyoufy.server.domain.dto.wms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsActiveWorkerGroupBase {
	private Long id;
	private Long workerAppId;

	private String name;

	private int maxActiveJobNum;
	private int freeActiveJobNum;

	private String workerLabels;
}
