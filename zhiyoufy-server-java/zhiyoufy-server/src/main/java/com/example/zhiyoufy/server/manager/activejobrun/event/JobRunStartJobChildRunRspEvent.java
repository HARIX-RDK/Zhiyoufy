package com.example.zhiyoufy.server.manager.activejobrun.event;

import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunStartJobChildRunRspEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunStartJobChildRunRspEvent";

	private JmsStartJobChildRunRsp startJobChildRunRsp;

	public JobRunStartJobChildRunRspEvent(JmsStartJobChildRunRsp startJobChildRunRsp) {
		this.setEventType(EVENT_TYPE);

		this.startJobChildRunRsp = startJobChildRunRsp;
	}
}
