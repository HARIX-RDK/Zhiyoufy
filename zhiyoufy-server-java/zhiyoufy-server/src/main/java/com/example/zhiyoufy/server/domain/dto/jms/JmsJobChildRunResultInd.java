package com.example.zhiyoufy.server.domain.dto.jms;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsJobChildRunResultInd {
	String messageId;
	String runGuid;
	Integer index;
	Boolean endOk;
	Boolean resultOk;
	Boolean passed;
	String jobOutputUrl;
	Map<String, Integer> childStatusCntMap;
}
