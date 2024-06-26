package com.example.zhiyoufy.server.domain.bo.wms;

import java.util.HashMap;
import java.util.Map;

import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsActiveApp {
	WmsWorkerApp workerApp;

	Map<Long, WmsActiveGroup> activeGroupMap = new HashMap<>();

	public WmsActiveApp(WmsWorkerApp workerApp) {
		this.workerApp = workerApp;
	}
}
