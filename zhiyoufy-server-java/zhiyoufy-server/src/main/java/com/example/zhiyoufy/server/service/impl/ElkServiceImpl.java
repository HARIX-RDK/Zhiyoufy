package com.example.zhiyoufy.server.service.impl;

import java.util.List;
import java.util.Map;

import com.example.zhiyoufy.common.elk.DefaultElkSwitchManager;
import com.example.zhiyoufy.server.domain.dto.elk.ElkSwitchState;
import com.example.zhiyoufy.server.service.ElkService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElkServiceImpl implements ElkService {
	@Autowired
	DefaultElkSwitchManager elkSwitchManager;

	@Override
	public void setAll(boolean allOn) {
		elkSwitchManager.setElkSwitchAllOn(allOn);
	}

	@Override
	public void setSome(Map<String, Boolean> someSwitches) {
		for (var entry : someSwitches.entrySet()) {
			if (entry.getValue()) {
				elkSwitchManager.getElkSwitchOns().add(entry.getKey());
			} else {
				elkSwitchManager.getElkSwitchOns().remove(entry.getKey());
			}
		}
	}

	@Override
	public ElkSwitchState getState() {
		ElkSwitchState elkSwitchState = new ElkSwitchState();
		elkSwitchState.setAllOn(elkSwitchManager.isElkSwitchAllOn());
		elkSwitchState.setSomeOns(List.copyOf(elkSwitchManager.getElkSwitchOns()));

		return elkSwitchState;
	}
}
