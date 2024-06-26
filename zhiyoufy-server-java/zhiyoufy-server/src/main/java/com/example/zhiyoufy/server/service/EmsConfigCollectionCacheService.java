package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.mbg.model.EmsConfigCollection;

public interface EmsConfigCollectionCacheService {
	EmsConfigCollection getConfigCollectionById(Long configCollectionId);
	EmsConfigCollection getConfigCollectionByEnvIdAndName(Long envId, String name);
	void removeConfigCollection(EmsConfigCollection configCollection);
	void setConfigCollection(EmsConfigCollection configCollection);

	void clearAll();
}
