package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsStopJobChildRunReq {
	private String runGuid;
	private int index;
}
