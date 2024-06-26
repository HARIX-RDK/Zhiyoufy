package com.example.zhiyoufy.server.manager.activejobrun.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunStartEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunStartEvent";

	public JobRunStartEvent() {
		this.setEventType(EVENT_TYPE);
	}
}
