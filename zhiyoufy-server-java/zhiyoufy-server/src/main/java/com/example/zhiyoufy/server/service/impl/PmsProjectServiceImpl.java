package com.example.zhiyoufy.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.PmsProjectMapper;
import com.example.zhiyoufy.mbg.mapper.PmsProjectUserRelationMapper;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectExample;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelationExample;
import com.example.zhiyoufy.server.dao.PmsProjectDao;
import com.example.zhiyoufy.server.dao.PmsProjectUserRelationDao;
import com.example.zhiyoufy.server.domain.bo.pms.PmsProjectDaoQueryParam;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectBase;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationUpdateParam;
import com.example.zhiyoufy.server.mapstruct.PmsProjectStructMapper;
import com.example.zhiyoufy.server.service.PmsJobFolderService;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
import com.example.zhiyoufy.server.service.PmsProjectCacheService;
import com.example.zhiyoufy.server.service.PmsProjectService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Getter
@Setter
@Slf4j
public class PmsProjectServiceImpl implements PmsProjectService {
	@Autowired
	PmsProjectMapper projectMapper;
	@Autowired
	PmsProjectUserRelationMapper projectUserRelationMapper;

	@Autowired
	PmsProjectDao projectDao;
	@Autowired
	PmsProjectUserRelationDao projectUserRelationDao;

	@Autowired
	PmsProjectCacheService projectCacheService;
	@Autowired
	PmsJobFolderService jobFolderService;
	@Autowired
	PmsJobTemplateService jobTemplateService;
	@Autowired
	UmsUserService userService;
	
	@Override
	@PreAuthorize("hasAuthority('projects.create')")
	@Transactional
	public PmsProject addProject(PmsProjectParam projectParam) {
		Assert.notNull(projectParam, "projectParam is null");
		Assert.hasText(projectParam.getName(), "name is empty");

		PmsProject pmsProject = getProjectByName(projectParam.getName());

		if (pmsProject != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		// add PmsProject
		pmsProject = PmsProjectStructMapper.INSTANCE
				.pmsProjectParamToPmsProject(projectParam);
		pmsProject.setCreatedBy(userDetails.getUsername());
		pmsProject.setModifiedBy(userDetails.getUsername());

		projectMapper.insertSelective(pmsProject);

		// add PmsProjectUserRelation
		PmsProjectUserRelation projectUserRelation = new PmsProjectUserRelation();
		projectUserRelation.setProjectId(pmsProject.getId());
		projectUserRelation.setUserId(userDetails.getUserId());
		projectUserRelation.setIsOwner(true);

		projectUserRelationMapper.insertSelective(projectUserRelation);

		// select to include DB generated time values
		pmsProject = projectMapper.selectByPrimaryKey(pmsProject.getId());

		projectCacheService.clearAll();

		return pmsProject;
	}

	@Override
	@PreAuthorize("hasAuthority('projects.delete')")
	@Transactional
	public int delProjectById(Long projectId) {
		PmsProject pmsProject = getProjectById(projectId);

		if (pmsProject == null) {
			return 0;
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation projectUserRelation =
					getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (projectUserRelation == null || !projectUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete job templates in this project
		jobTemplateService.delJobTemplatesByProjectId(projectId);

		// delete job folders in this project
		jobFolderService.delJobFoldersByProjectId(projectId);

		// delete PmsProjectUserRelation
		{
			PmsProjectUserRelationExample example = new PmsProjectUserRelationExample();
			PmsProjectUserRelationExample.Criteria criteria = example
					.createCriteria();

			criteria.andProjectIdEqualTo(projectId);

			projectUserRelationMapper.deleteByExample(example);
		}

		// delete PmsProject
		int deleted = projectMapper.deleteByPrimaryKey(projectId);

		projectCacheService.clearAll();

		return deleted;
	}

	@Override
	public PmsProject getProjectById(Long projectId) {
		PmsProject project = projectCacheService.getProjectById(projectId);

		if (project != null) {
			return project;
		}

		project = projectMapper.selectByPrimaryKey(projectId);

		if (project != null) {
			projectCacheService.setProject(project);
		}

		return project;
	}

	@Override
	public PmsProject getProjectByName(String name) {
		PmsProject pmsProject = getProjectByNameFromDb(name);

		return pmsProject;
	}

	@Override
	@PreAuthorize("hasAuthority('projects.get')")
	public CommonPage<PmsProjectFull> getProjectList(PmsProjectQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		if (!CheckUtils.isTrue(queryParam.getAllUsers())) {
			return getProjectListForCurUser(queryParam, pageSize, pageNum);
		}

		return getProjectListForAllUsers(queryParam, pageSize, pageNum);
	}

	@Override
	public List<PmsProjectBase> getProjectBaseList() {
		UmsUserDetails userDetails = userService.getUserDetails();

		List<PmsProjectBase> projectBaseList =
				projectDao.getProjectBaseListByUserId(userDetails.getUserId());

		return projectBaseList;
	}

	@Override
	public Set<Long> getProjectIdSet() {
		UmsUserDetails userDetails = userService.getUserDetails();

		return getProjectIdSet(userDetails.getUserId());
	}

	@Override
	public Set<Long> getProjectIdSet(Long userId) {
		Set<Long> projectIdSet = projectCacheService.getProjectIdSetByUserId(userId);

		if (projectIdSet != null) {
			return projectIdSet;
		}

		List<PmsProjectBase> projectBaseList =
				projectDao.getProjectBaseListByUserId(userId);

		projectIdSet = projectBaseList.stream().map(PmsProjectBase::getId)
				.collect(Collectors.toSet());

		projectCacheService.setProjectIdSetByUserId(userId, projectIdSet);

		return projectIdSet;
	}

	@Override
	@PreAuthorize("hasAuthority('projects.update')")
	public int updateProject(Long id, PmsProjectUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		PmsProject targetProject = getProjectById(id);

		if (targetProject == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation projectUserRelation =
					getProjectUserRelationByProjectIdAndUserId(id, userDetails.getUserId());

			if (projectUserRelation == null || (!projectUserRelation.getIsOwner() &&
					!projectUserRelation.getIsEditor())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsProject pmsProject = PmsProjectStructMapper.INSTANCE
				.pmsProjectUpdateParamToPmsProject(updateParam);
		pmsProject.setId(id);

		projectCacheService.clearAll();

		return projectMapper.updateByPrimaryKeySelective(pmsProject);
	}

	@Override
	public PmsProjectUserRelation getProjectUserRelationByProjectIdAndUserId(Long projectId, Long userId) {
		Map<Long, PmsProjectUserRelation> relationMap = getProjectUserRelationMapByUserId(userId);

		return relationMap.get(projectId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner')")
	public int addProjectUserRelation(PmsProjectUserRelationFull relationFull) {
		Assert.isNull(relationFull.getId(), "id should be null");

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation curUserRelation =
					getProjectUserRelationByProjectIdAndUserId(
							relationFull.getProjectId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsProjectUserRelation relation = PmsProjectStructMapper.INSTANCE
				.relationFullToRelation(relationFull);
		relation.setId(null);
		int cnt = projectUserRelationMapper.insert(relation);

		projectCacheService.removeProjectIdSetByUserId(relationFull.getUserId());

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner')")
	@Transactional
	public int delProjectUserRelationById(Long id) {
		PmsProjectUserRelation relation =
				projectUserRelationMapper.selectByPrimaryKey(id);

		if (relation == null) {
			Asserts.fail(CommonErrorCode.RES_NOT_FOUND);
		}

		if (relation.getIsOwner()) {
			PmsProjectUserRelationExample example =
					new PmsProjectUserRelationExample();
			PmsProjectUserRelationExample.Criteria criteria = example.createCriteria();

			criteria.andProjectIdEqualTo(relation.getProjectId())
					.andIsOwnerEqualTo(true);

			long ownerCnt = projectUserRelationMapper.countByExample(example);

			if (ownerCnt == 1) {
				Asserts.fail(CommonErrorCode.RES_DELETE_LAST_OWNER_NOT_ALLOWED);
			}
		}

		int cnt = projectUserRelationMapper.deleteByPrimaryKey(id);

		projectCacheService.removeProjectIdSetByUserId(relation.getUserId());

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner')")
	public CommonPage<PmsProjectUserRelationFull> getProjectUserRelationListByProjectId(
			Long projectId, Integer pageSize, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);

		List<PmsProjectUserRelationFull> relationFullList =
				projectUserRelationDao.getUserRelationListByProjectId(projectId);

		return CommonPage.restPage(relationFullList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner')")
	public int updateProjectUserRelation(Long id, PmsProjectUserRelationUpdateParam updateParam) {
		PmsProjectUserRelation targetRelation = projectUserRelationMapper.selectByPrimaryKey(id);

		if (targetRelation == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation curUserRelation =
					getProjectUserRelationByProjectIdAndUserId(
							targetRelation.getProjectId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsProjectUserRelation relation = PmsProjectStructMapper.INSTANCE
				.updateParamToRelation(updateParam);
		relation.setId(id);

		return projectUserRelationMapper.updateByPrimaryKeySelective(relation);
	}

	private CommonPage<PmsProjectFull> getProjectListForAllUsers(
			PmsProjectQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		PmsProjectExample example = new PmsProjectExample();
		PmsProjectExample.Criteria criteria = example.createCriteria();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			if (CheckUtils.isTrue(queryParam.getExactMatch())) {
				criteria.andNameLike(queryParam.getKeyword());
			} else {
				criteria.andNameLike("%" + queryParam.getKeyword() + "%");
			}
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			example.setOrderByClause(orderBy);
		}

		PageHelper.startPage(pageNum, pageSize);

		List<PmsProject> projectList =
				projectMapper.selectByExample(example);
		List<PmsProjectFull> projectFullList = projectList.stream()
				.map(project -> {
					PmsProjectFull projectFull = PmsProjectStructMapper.INSTANCE
							.pmsProjectToPmsProjectFull(project);
					return projectFull;
				})
				.collect(Collectors.toList());

		return CommonPage.restPage(projectFullList, projectList);
	}

	private CommonPage<PmsProjectFull> getProjectListForCurUser(
			PmsProjectQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		PmsProjectDaoQueryParam daoQueryParam = new PmsProjectDaoQueryParam();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			if (CheckUtils.isTrue(queryParam.getExactMatch())) {
				daoQueryParam.setKeyword(queryParam.getKeyword());
			} else {
				daoQueryParam.setKeyword("%" + queryParam.getKeyword() + "%");
			}
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			daoQueryParam.setOrderByClause(orderBy);
		}

		PageHelper.startPage(pageNum, pageSize);

		List<PmsProjectFull> projectFullList =
				projectDao.getProjectListByUserId(
						userDetails.getUserId(), daoQueryParam);

		return CommonPage.restPage(projectFullList);
	}

	private Map<Long, PmsProjectUserRelation> getProjectUserRelationMapByUserId(Long userId) {
		Map<Long, PmsProjectUserRelation> relationMap =
				projectCacheService.getProjectUserRelationMapByUserId(userId);

		if (relationMap != null) {
			return relationMap;
		}

		PmsProjectUserRelationExample example = new PmsProjectUserRelationExample();
		PmsProjectUserRelationExample.Criteria criteria = example.createCriteria();

		criteria.andUserIdEqualTo(userId);

		List<PmsProjectUserRelation> relationList =
				projectUserRelationMapper.selectByExample(example);

		Map<Long, PmsProjectUserRelation> relationMapNew = new HashMap<>();
		relationList.forEach(relation -> {
			relationMapNew.put(relation.getProjectId(), relation);
		});

		projectCacheService.setProjectUserRelationMapByUserId(userId, relationMapNew);

		return relationMapNew;
	}

	private PmsProject getProjectByNameFromDb(String name) {
		PmsProjectExample example = new PmsProjectExample();
		example.createCriteria().andNameEqualTo(name);

		List<PmsProject> projectListList = projectMapper.selectByExample(example);

		if (projectListList != null && projectListList.size() > 0) {
			PmsProject project = projectListList.get(0);

			projectCacheService.setProject(project);

			return project;
		}

		return null;
	}
}
