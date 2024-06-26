package com.example.zhiyoufy.server.service.impl;

import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.server.service.EmsConfigSingleCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmsConfigSingleCacheServiceImpl implements EmsConfigSingleCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_ConfigSingleById = "configSingleById:";
	private final String CACHE_KEY_ConfigSingleByEnvIdAndName = "configSingleByEnvIdAndName:";
	private final String separator = "__";
	//endregion

	@Override
	public EmsConfigSingle getConfigSingleById(Long configSingleId) {
		String key = CACHE_KEY_ConfigSingleById + configSingleId;
		return (EmsConfigSingle) cache.getIfPresent(key);
	}

	@Override
	public EmsConfigSingle getConfigSingleByEnvIdAndName(Long envId, String name) {
		String key = CACHE_KEY_ConfigSingleByEnvIdAndName + envId + separator + name;
		return (EmsConfigSingle) cache.getIfPresent(key);
	}

	@Override
	public void removeConfigSingle(EmsConfigSingle configSingle) {
		String key = CACHE_KEY_ConfigSingleById + configSingle.getId();
		cache.invalidate(key);
		key = CACHE_KEY_ConfigSingleByEnvIdAndName + configSingle.getEnvironmentId()
				+ separator + configSingle.getName();
		cache.invalidate(key);
	}

	@Override
	public void setConfigSingle(EmsConfigSingle configSingle) {
		String key = CACHE_KEY_ConfigSingleById + configSingle.getId();
		cache.put(key, configSingle);
		key = CACHE_KEY_ConfigSingleByEnvIdAndName + configSingle.getEnvironmentId()
				+ separator + configSingle.getName();
		cache.put(key, configSingle);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
