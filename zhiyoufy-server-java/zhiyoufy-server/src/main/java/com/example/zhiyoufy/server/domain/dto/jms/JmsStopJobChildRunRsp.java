package com.example.zhiyoufy.server.domain.dto.jms;

import com.example.zhiyoufy.common.api.ErrorInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsStopJobChildRunRsp {
	private ErrorInfo error;

	private String runGuid;
	private Integer index;
	private String messageId;
}
