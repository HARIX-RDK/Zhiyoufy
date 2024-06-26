package com.example.zhiyoufy.server.service.impl;

import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.service.WmsWorkerGroupCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WmsWorkerGroupCacheServiceImpl implements WmsWorkerGroupCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_WorkerGroupById = "workerGroupById:";
	private final String CACHE_KEY_WorkerGroupByWorkerAppIdAndName = "workerGroupByWorkerAppIdAndName:";
	private final String separator = "__";
	//endregion

	@Override
	public WmsWorkerGroup getWorkerGroupById(Long workerGroupId) {
		String key = CACHE_KEY_WorkerGroupById + workerGroupId;
		return (WmsWorkerGroup) cache.getIfPresent(key);
	}

	@Override
	public WmsWorkerGroup getWorkerGroupByWorkerAppIdAndName(Long envId, String name) {
		String key = CACHE_KEY_WorkerGroupByWorkerAppIdAndName + envId + separator + name;
		return (WmsWorkerGroup) cache.getIfPresent(key);
	}

	@Override
	public void removeWorkerGroup(WmsWorkerGroup workerGroup) {
		String key = CACHE_KEY_WorkerGroupById + workerGroup.getId();
		cache.invalidate(key);
		key = CACHE_KEY_WorkerGroupByWorkerAppIdAndName + workerGroup.getWorkerAppId()
				+ separator + workerGroup.getName();
		cache.invalidate(key);
	}

	@Override
	public void setWorkerGroup(WmsWorkerGroup workerGroup) {
		String key = CACHE_KEY_WorkerGroupById + workerGroup.getId();
		cache.put(key, workerGroup);
		key = CACHE_KEY_WorkerGroupByWorkerAppIdAndName + workerGroup.getWorkerAppId()
				+ separator + workerGroup.getName();
		cache.put(key, workerGroup);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
