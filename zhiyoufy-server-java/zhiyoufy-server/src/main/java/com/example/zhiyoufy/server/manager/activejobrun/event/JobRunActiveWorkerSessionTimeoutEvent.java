package com.example.zhiyoufy.server.manager.activejobrun.event;

import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunActiveWorkerSessionTimeoutEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunActiveWorkerSessionTimeoutEvent";

	private WmsActiveWorkerBase activeWorkerBase;

	public JobRunActiveWorkerSessionTimeoutEvent(WmsActiveWorkerBase activeWorkerBase) {
		this.setEventType(EVENT_TYPE);

		this.activeWorkerBase = activeWorkerBase;
	}
}
