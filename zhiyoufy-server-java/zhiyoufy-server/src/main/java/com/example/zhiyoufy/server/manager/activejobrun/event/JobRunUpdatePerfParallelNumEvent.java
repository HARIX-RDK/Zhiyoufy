package com.example.zhiyoufy.server.manager.activejobrun.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunUpdatePerfParallelNumEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunUpdatePerfParallelNumEvent";

	private int perfParallelNum;

	public JobRunUpdatePerfParallelNumEvent() {
		this.setEventType(EVENT_TYPE);
	}
}
