package com.example.zhiyoufy.server.manager.activejobrun.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunStopEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunStopEvent";

	public JobRunStopEvent() {
		this.setEventType(EVENT_TYPE);
	}
}
