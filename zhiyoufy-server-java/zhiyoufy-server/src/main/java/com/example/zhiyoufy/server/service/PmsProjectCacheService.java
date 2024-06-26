package com.example.zhiyoufy.server.service;

import java.util.Map;
import java.util.Set;

import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;

public interface PmsProjectCacheService {
	PmsProject getProjectById(Long projectId);
	void removeProject(PmsProject project);
	void setProject(PmsProject pmsProject);

	Set<Long> getProjectIdSetByUserId(Long userId);
	void removeProjectIdSetByUserId(Long userId);
	void setProjectIdSetByUserId(Long userId, Set<Long> projectIdSet);

	Map<Long, PmsProjectUserRelation> getProjectUserRelationMapByUserId(Long userId);
	void removeProjectUserRelationMapByUserId(Long userId);
	void setProjectUserRelationMapByUserId(Long userId,
			Map<Long, PmsProjectUserRelation> relationMap);

	void clearAll();
}
