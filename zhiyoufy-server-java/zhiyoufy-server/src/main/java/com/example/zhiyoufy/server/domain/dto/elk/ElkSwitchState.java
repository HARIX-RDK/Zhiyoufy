package com.example.zhiyoufy.server.domain.dto.elk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElkSwitchState {
	private boolean allOn;
	private List<String> someOns;
}
