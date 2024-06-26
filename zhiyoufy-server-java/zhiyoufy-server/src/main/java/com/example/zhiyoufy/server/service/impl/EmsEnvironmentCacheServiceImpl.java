package com.example.zhiyoufy.server.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.service.EmsEnvironmentCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmsEnvironmentCacheServiceImpl implements EmsEnvironmentCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_ENVIRONMENT = "environment:";
	private final String CACHE_KEY_ENV_USER_RELATIONS = "envUserRelations:";
	//endregion

	@Override
	public EmsEnvironment getEnvironmentById(Long envId) {
		String key = CACHE_KEY_ENVIRONMENT + envId;
		return (EmsEnvironment) cache.getIfPresent(key);
	}

	@Override
	public void removeEnvironment(EmsEnvironment environment) {
		String key = CACHE_KEY_ENVIRONMENT + environment.getId();
		cache.invalidate(key);
	}

	@Override
	public void setEnvironment(EmsEnvironment environment) {
		String key = CACHE_KEY_ENVIRONMENT + environment.getId();
		cache.put(key, environment);
	}

	@Override
	public Map<Long, EmsEnvironmentUserRelation> getEnvironmentUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_ENV_USER_RELATIONS + userId;
		return (Map<Long, EmsEnvironmentUserRelation>) cache.getIfPresent(key);
	}

	@Override
	public void removeEnvironmentUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_ENV_USER_RELATIONS + userId;
		cache.invalidate(key);
	}

	@Override
	public void setEnvironmentUserRelationMapByUserId(Long userId,
			Map<Long, EmsEnvironmentUserRelation> relationMap) {
		String key = CACHE_KEY_ENV_USER_RELATIONS + userId;
		cache.put(key, relationMap);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
