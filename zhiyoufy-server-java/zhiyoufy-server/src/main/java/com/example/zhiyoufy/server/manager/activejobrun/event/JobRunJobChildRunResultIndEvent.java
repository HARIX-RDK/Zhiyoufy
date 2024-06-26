package com.example.zhiyoufy.server.manager.activejobrun.event;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRunJobChildRunResultIndEvent extends JobRunEvent {
	public final static String EVENT_TYPE = "JobRunJobChildRunResultIndEvent";

	private JmsJobChildRunResultInd jobChildRunResultInd;

	public JobRunJobChildRunResultIndEvent(JmsJobChildRunResultInd jobChildRunResultInd) {
		this.setEventType(EVENT_TYPE);

		this.jobChildRunResultInd = jobChildRunResultInd;
	}
}
