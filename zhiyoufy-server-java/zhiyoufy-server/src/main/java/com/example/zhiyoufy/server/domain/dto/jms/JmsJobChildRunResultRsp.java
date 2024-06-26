package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsJobChildRunResultRsp {
	String messageId;
	String runGuid;
	Integer index;
}
