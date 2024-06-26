package com.example.zhiyoufy.server.service;

import java.util.List;
import java.util.Set;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectBase;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationUpdateParam;

public interface PmsProjectService {
	PmsProject addProject(PmsProjectParam projectParam);

	int delProjectById(Long projectId);

	PmsProject getProjectById(Long projectId);

	PmsProject getProjectByName(String name);

	CommonPage<PmsProjectFull> getProjectList(PmsProjectQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<PmsProjectBase> getProjectBaseList();
	Set<Long> getProjectIdSet();
	Set<Long> getProjectIdSet(Long userId);

	int updateProject(Long id, PmsProjectUpdateParam updateParam);

	PmsProjectUserRelation getProjectUserRelationByProjectIdAndUserId(
			Long projectId, Long userId);

	int addProjectUserRelation(PmsProjectUserRelationFull relationFull);
	int delProjectUserRelationById(Long id);
	CommonPage<PmsProjectUserRelationFull> getProjectUserRelationListByProjectId(
			Long projectId, Integer pageSize, Integer pageNum);
	int updateProjectUserRelation(Long id,
			PmsProjectUserRelationUpdateParam updateParam);
}
