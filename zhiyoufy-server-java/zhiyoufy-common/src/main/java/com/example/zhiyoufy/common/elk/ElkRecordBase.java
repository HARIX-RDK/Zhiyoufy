package com.example.zhiyoufy.common.elk;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import com.example.zhiyoufy.common.api.ErrorInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * elk记录的类型定义，其中type用来区分不同类型，
 * tags用来方便过滤含有或不含有某些tags的记录，
 * rootReqId类似于zipkin的traceid，用来在分布式环境中标识一个消息的整个处理链，
 * timestamp为写log的时间，reqId为当前处理段或叫span的id，
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ElkRecordBase {
	private OffsetDateTime timestamp;
	private String rootReqId;
	private String reqId;
	private String type;
	private String[] tags;
	private String principalName;

	private ErrorInfo error;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<String> traceRecords;
	private Long costTimeMs;

	private StandardElkData stdData;

	public void setDuration(Duration duration) {
		this.costTimeMs = duration.toMillis();
	}

	public abstract void setElkData(ElkData elkData);
}
