package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;

public interface WmsWorkerGroupCacheService {
	WmsWorkerGroup getWorkerGroupById(Long workerGroupId);
	WmsWorkerGroup getWorkerGroupByWorkerAppIdAndName(Long workerAppId, String name);
	void removeWorkerGroup(WmsWorkerGroup workerGroup);
	void setWorkerGroup(WmsWorkerGroup workerGroup);

	void clearAll();
}
