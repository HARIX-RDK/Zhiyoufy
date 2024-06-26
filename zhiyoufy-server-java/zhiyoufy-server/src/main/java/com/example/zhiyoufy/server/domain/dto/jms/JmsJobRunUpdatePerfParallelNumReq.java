package com.example.zhiyoufy.server.domain.dto.jms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmsJobRunUpdatePerfParallelNumReq {
	@NotEmpty
	@Size(min = 16)
	private String runGuid;

	@Min(0)
	private int perfParallelNum;
}
