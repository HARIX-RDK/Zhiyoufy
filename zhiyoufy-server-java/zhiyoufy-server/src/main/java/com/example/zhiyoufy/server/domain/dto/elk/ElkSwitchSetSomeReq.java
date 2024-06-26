package com.example.zhiyoufy.server.domain.dto.elk;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElkSwitchSetSomeReq {
	private Map<String, Boolean> someSwitches;
}
