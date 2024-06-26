package com.example.zhiyoufy.server.service;

import java.util.Map;

import com.example.zhiyoufy.server.domain.dto.elk.ElkSwitchState;

public interface ElkService {
	void setAll(boolean allOn);
	void setSome(Map<String, Boolean> someSwitches);

	ElkSwitchState getState();
}
