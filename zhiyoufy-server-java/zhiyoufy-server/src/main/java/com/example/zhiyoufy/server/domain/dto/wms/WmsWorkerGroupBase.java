package com.example.zhiyoufy.server.domain.dto.wms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsWorkerGroupBase {
	private Long id;
	private Long workerAppId;

	private String name;
}
