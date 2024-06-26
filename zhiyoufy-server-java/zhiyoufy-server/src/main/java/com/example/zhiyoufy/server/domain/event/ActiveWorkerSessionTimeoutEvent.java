package com.example.zhiyoufy.server.domain.event;

import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveWorkerSessionTimeoutEvent {
	private WmsActiveWorkerBase activeWorkerBase;
}
