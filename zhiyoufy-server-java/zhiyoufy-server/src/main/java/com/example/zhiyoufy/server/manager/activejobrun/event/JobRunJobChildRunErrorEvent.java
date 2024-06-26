package com.example.zhiyoufy.server.manager.activejobrun.event;

import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobChildRun;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunJobChildRunErrorEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunJobChildRunErrorEvent";

	private Reason reason;
	private Integer index;

	public JobRunJobChildRunErrorEvent(Reason reason, Integer index) {
		this.setEventType(EVENT_TYPE);

		this.reason = reason;
		this.index = index;
	}

	public enum Reason {
		WaitStartJobRspTimeout,
		WaitResultIndTimeout,
		WaitStopJobRspTimeout,
		ConvertHoconToJsonFailed,
		StartJobChildRunFailed
	}
}
