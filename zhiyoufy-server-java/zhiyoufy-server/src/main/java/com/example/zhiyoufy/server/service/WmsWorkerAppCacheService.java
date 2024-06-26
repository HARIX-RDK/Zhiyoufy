package com.example.zhiyoufy.server.service;

import java.util.Map;

import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;

public interface WmsWorkerAppCacheService {
	WmsWorkerApp getWorkerAppById(Long workerAppId);
	void removeWorkerApp(WmsWorkerApp workerApp);
	void setWorkerApp(WmsWorkerApp workerApp);

	Map<Long, WmsWorkerAppUserRelation> getWorkerAppUserRelationMapByUserId(Long userId);
	void removeWorkerAppUserRelationMapByUserId(Long userId);
	void setWorkerAppUserRelationMapByUserId(Long userId,
			Map<Long, WmsWorkerAppUserRelation> relationMap);

	void clearAll();
}
