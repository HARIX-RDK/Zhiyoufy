package com.example.zhiyoufy.server.service;

import java.util.Map;

import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;

public interface EmsEnvironmentCacheService {
	EmsEnvironment getEnvironmentById(Long envId);
	void removeEnvironment(EmsEnvironment environment);
	void setEnvironment(EmsEnvironment emsEnvironment);

	Map<Long, EmsEnvironmentUserRelation>  getEnvironmentUserRelationMapByUserId(Long userId);
	void removeEnvironmentUserRelationMapByUserId(Long userId);
	void setEnvironmentUserRelationMapByUserId(Long userId,
			Map<Long, EmsEnvironmentUserRelation> relationMap);

	void clearAll();
}
