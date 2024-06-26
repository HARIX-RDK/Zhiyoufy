package com.example.zhiyoufy.server.manager.activejobrun.event;

import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunStopJobChildRunRspEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunStopJobChildRunRspEvent";

	private JmsStopJobChildRunRsp stopJobChildRunRsp;

	public JobRunStopJobChildRunRspEvent(JmsStopJobChildRunRsp stopJobChildRunRsp) {
		this.setEventType(EVENT_TYPE);

		this.stopJobChildRunRsp = stopJobChildRunRsp;
	}
}
