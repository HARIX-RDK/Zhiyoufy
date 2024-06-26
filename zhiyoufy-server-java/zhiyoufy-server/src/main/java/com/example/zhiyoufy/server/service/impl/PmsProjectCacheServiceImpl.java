package com.example.zhiyoufy.server.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.service.PmsProjectCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PmsProjectCacheServiceImpl implements PmsProjectCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private final String CACHE_KEY_PROJECT = "project:";
	private final String CACHE_KEY_PROJECT_USER_RELATIONS = "projectUserRelations:";
	private final String CACHE_KEY_PROJECT_ID_SET = "projectIdSet:";
	//endregion

	@Override
	public PmsProject getProjectById(Long projectId) {
		String key = CACHE_KEY_PROJECT + projectId;
		return (PmsProject) cache.getIfPresent(key);
	}

	@Override
	public void removeProject(PmsProject project) {
		String key = CACHE_KEY_PROJECT + project.getId();
		cache.invalidate(key);
	}

	@Override
	public void setProject(PmsProject project) {
		String key = CACHE_KEY_PROJECT + project.getId();
		cache.put(key, project);
	}

	@Override
	public Set<Long> getProjectIdSetByUserId(Long userId) {
		String key = CACHE_KEY_PROJECT_ID_SET + userId;
		return (Set<Long>)cache.getIfPresent(key);
	}

	@Override
	public void removeProjectIdSetByUserId(Long userId) {
		String key = CACHE_KEY_PROJECT_ID_SET + userId;
		cache.invalidate(key);
	}

	@Override
	public void setProjectIdSetByUserId(Long userId, Set<Long> projectIdSet) {
		String key = CACHE_KEY_PROJECT_ID_SET + userId;
		cache.put(key, projectIdSet);
	}

	@Override
	public Map<Long, PmsProjectUserRelation> getProjectUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_PROJECT_USER_RELATIONS + userId;
		return (Map<Long, PmsProjectUserRelation>) cache.getIfPresent(key);
	}

	@Override
	public void removeProjectUserRelationMapByUserId(Long userId) {
		String key = CACHE_KEY_PROJECT_USER_RELATIONS + userId;
		cache.invalidate(key);
	}

	@Override
	public void setProjectUserRelationMapByUserId(Long userId,
			Map<Long, PmsProjectUserRelation> relationMap) {
		String key = CACHE_KEY_PROJECT_USER_RELATIONS + userId;
		cache.put(key, relationMap);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
	}
}
