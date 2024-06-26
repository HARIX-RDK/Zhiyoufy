package com.example.zhiyoufy.server.service.impl;

import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.server.service.EmsConfigCollectionCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmsConfigCollectionCacheServiceImpl implements EmsConfigCollectionCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_ConfigCollectionById = "configCollectionById:";
	private final String CACHE_KEY_ConfigCollectionByEnvIdAndName = "configCollectionByEnvIdAndName:";
	private final String separator = "__";
	//endregion
	
	@Override
	public EmsConfigCollection getConfigCollectionById(Long configCollectionId) {
		String key = CACHE_KEY_ConfigCollectionById + configCollectionId;
		return (EmsConfigCollection) cache.getIfPresent(key);
	}

	@Override
	public EmsConfigCollection getConfigCollectionByEnvIdAndName(Long envId, String name) {
		String key = CACHE_KEY_ConfigCollectionByEnvIdAndName + envId + separator + name;
		return (EmsConfigCollection) cache.getIfPresent(key);
	}

	@Override
	public void removeConfigCollection(EmsConfigCollection configCollection) {
		String key = CACHE_KEY_ConfigCollectionById + configCollection.getId();
		cache.invalidate(key);
		key = CACHE_KEY_ConfigCollectionByEnvIdAndName + configCollection.getEnvironmentId()
				+ separator + configCollection.getName();
		cache.invalidate(key);
	}

	@Override
	public void setConfigCollection(EmsConfigCollection configCollection) {
		String key = CACHE_KEY_ConfigCollectionById + configCollection.getId();
		cache.put(key, configCollection);
		key = CACHE_KEY_ConfigCollectionByEnvIdAndName + configCollection.getEnvironmentId()
				+ separator + configCollection.getName();
		cache.put(key, configCollection);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
