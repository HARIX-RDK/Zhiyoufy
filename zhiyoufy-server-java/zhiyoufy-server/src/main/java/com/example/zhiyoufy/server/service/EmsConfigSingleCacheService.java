package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.mbg.model.EmsConfigSingle;

public interface EmsConfigSingleCacheService {
	EmsConfigSingle getConfigSingleById(Long configSingleId);
	EmsConfigSingle getConfigSingleByEnvIdAndName(Long envId, String name);
	void removeConfigSingle(EmsConfigSingle configSingle);
	void setConfigSingle(EmsConfigSingle configSingle);

	void clearAll();
}
