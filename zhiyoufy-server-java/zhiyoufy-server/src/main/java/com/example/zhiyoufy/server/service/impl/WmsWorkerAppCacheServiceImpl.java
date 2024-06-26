package com.example.zhiyoufy.server.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.server.service.WmsWorkerAppCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WmsWorkerAppCacheServiceImpl implements WmsWorkerAppCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_WORKER_APP = "workerApp:";
	private final String CACHE_KEY_WORKER_APP_USER_RELATIONS = "workerAppUserRelations:";
	//endregion

	@Override
	public WmsWorkerApp getWorkerAppById(Long workerAppId) {
		String key = CACHE_KEY_WORKER_APP + workerAppId;
		return (WmsWorkerApp) cache.getIfPresent(key);
	}

	@Override
	public void removeWorkerApp(WmsWorkerApp workerApp) {
		String key = CACHE_KEY_WORKER_APP + workerApp.getId();
		cache.invalidate(key);
	}

	@Override
	public void setWorkerApp(WmsWorkerApp workerApp) {
		String key = CACHE_KEY_WORKER_APP + workerApp.getId();
		cache.put(key, workerApp);
	}

	@Override
	public Map<Long, WmsWorkerAppUserRelation> getWorkerAppUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_WORKER_APP_USER_RELATIONS + userId;
		return (Map<Long, WmsWorkerAppUserRelation>) cache.getIfPresent(key);
	}

	@Override
	public void removeWorkerAppUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_WORKER_APP_USER_RELATIONS + userId;
		cache.invalidate(key);
	}

	@Override
	public void setWorkerAppUserRelationMapByUserId(Long userId,
			Map<Long, WmsWorkerAppUserRelation> relationMap) {
		String key = CACHE_KEY_WORKER_APP_USER_RELATIONS + userId;
		cache.put(key, relationMap);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
