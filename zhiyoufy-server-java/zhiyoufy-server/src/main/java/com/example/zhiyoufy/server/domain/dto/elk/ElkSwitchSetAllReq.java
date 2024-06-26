package com.example.zhiyoufy.server.domain.dto.elk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElkSwitchSetAllReq {
	private Boolean allOn;
}
