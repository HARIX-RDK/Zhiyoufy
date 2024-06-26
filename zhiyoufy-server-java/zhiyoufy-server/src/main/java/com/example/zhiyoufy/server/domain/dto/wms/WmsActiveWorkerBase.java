package com.example.zhiyoufy.server.domain.dto.wms;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsActiveWorkerBase {
	private boolean disconnected;
	private Date connectTime;
	private Date disconnectTime;
	private String sessionId;

	private String appRunId;
	private Date appStartTimestamp;

	private String workerName;

	private int maxActiveJobNum;
	private int freeActiveJobNum;
}
