package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.PmsJobTemplateMapper;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsJobTemplateExample;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateUpdateParam;
import com.example.zhiyoufy.server.mapstruct.PmsProjectStructMapper;
import com.example.zhiyoufy.server.service.PmsJobFolderService;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
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
public class PmsJobTemplateServiceImpl implements PmsJobTemplateService {
	@Autowired
	PmsProjectService projectService;
	@Autowired
	PmsJobFolderService jobFolderService;
	@Autowired
	UmsUserService userService;

	@Autowired
	PmsJobTemplateMapper jobTemplateMapper;
	
	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	public PmsJobTemplate addJobTemplate(PmsJobTemplateParam jobTemplateParam) {
		Assert.notNull(jobTemplateParam, "jobTemplateParam is null");
		Assert.notNull(jobTemplateParam.getProjectId(), "projectId is null");
		Assert.notNull(jobTemplateParam.getFolderId(), "folderId is null");
		Assert.hasText(jobTemplateParam.getName(), "name is empty");

		Long projectId = jobTemplateParam.getProjectId();

		PmsJobTemplate jobTemplate = getJobTemplateByProjectIdAndName(
				projectId, jobTemplateParam.getName());

		if (jobTemplate != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		PmsProject pmsProject = projectService.getProjectById(projectId);

		if (pmsProject == null ||
				!pmsProject.getName().equals(jobTemplateParam.getProjectName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long folderId = jobTemplateParam.getFolderId();
		PmsJobFolder jobFolder = jobFolderService.getJobFolderById(folderId);

		if (jobFolder == null ||
				!jobFolder.getName().equals(jobTemplateParam.getFolderName()) ||
				!jobFolder.getProjectId().equals(projectId)) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// add PmsJobTemplate
		jobTemplate = PmsProjectStructMapper.INSTANCE
				.pmsJobTemplateParamToPmsJobTemplate(jobTemplateParam);
		jobTemplate.setCreatedBy(userDetails.getUsername());
		jobTemplate.setModifiedBy(userDetails.getUsername());

		jobTemplateMapper.insertSelective(jobTemplate);

		return jobTemplate;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	@Transactional
	public DeleteInfo delJobTemplateById(Long jobTemplateId) {
		PmsJobTemplate jobTemplate = getJobTemplateById(jobTemplateId);

		if (jobTemplate == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long projectId = jobTemplate.getProjectId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		int deleted = jobTemplateMapper.deleteByPrimaryKey(jobTemplateId);

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(jobTemplate.getName())
				.projectId(projectId)
				.build();

		return deleteInfo;
	}

	@Override
	public int delJobTemplatesByFolderId(Long folderId) {
		PmsJobTemplateExample example = new PmsJobTemplateExample();
		PmsJobTemplateExample.Criteria criteria = example.createCriteria();

		criteria.andFolderIdEqualTo(folderId);

		int deleted = jobTemplateMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public int delJobTemplatesByProjectId(Long projectId) {
		PmsJobTemplateExample example = new PmsJobTemplateExample();
		PmsJobTemplateExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		int deleted = jobTemplateMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public PmsJobTemplate getJobTemplateById(Long jobTemplateId) {
		PmsJobTemplate jobTemplate = jobTemplateMapper.selectByPrimaryKey(jobTemplateId);

		return jobTemplate;
	}

	@Override
	public PmsJobTemplate getJobTemplateByProjectIdAndName(Long projectId, String name) {
		PmsJobTemplate jobTemplate = getJobTemplateByProjectIdAndNameFromDb(projectId, name);

		return jobTemplate;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor') "
			+ "|| hasAuthority('roles/project.viewer')")
	public CommonPage<PmsJobTemplate> getJobTemplateList(Long projectId,
			PmsJobTemplateQueryParam queryParam, Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (queryParam.getFolderId() != null) {
			PmsJobFolder jobFolder = jobFolderService.getJobFolderById(queryParam.getFolderId());

			if (jobFolder == null ||
					!jobFolder.getProjectId().equals(projectId)) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
			}
		}

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsJobTemplateExample example = new PmsJobTemplateExample();
		PmsJobTemplateExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		if (queryParam.getFolderId() != null) {
			criteria.andFolderIdEqualTo(queryParam.getFolderId());
		}

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

		List<PmsJobTemplate> jobTemplateList = jobTemplateMapper.selectByExample(example);

		return CommonPage.restPage(jobTemplateList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	public UpdateInfo updateJobTemplate(Long id, PmsJobTemplateUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		PmsJobTemplate targetJobTemplate = getJobTemplateById(id);

		if (targetJobTemplate == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long projectId = targetJobTemplate.getProjectId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsJobTemplate jobTemplate = PmsProjectStructMapper.INSTANCE
				.pmsJobTemplateUpdateParamToPmsJobTemplate(updateParam);
		jobTemplate.setId(id);
		jobTemplate.setModifiedBy(userDetails.getUsername());

		int updated = jobTemplateMapper.updateByPrimaryKeySelective(jobTemplate);

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetJobTemplate.getName())
				.projectId(projectId)
				.build();

		return updateInfo;
	}

	private PmsJobTemplate getJobTemplateByProjectIdAndNameFromDb(Long projectId, String name) {
		PmsJobTemplateExample example = new PmsJobTemplateExample();
		PmsJobTemplateExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId).andNameEqualTo(name);

		List<PmsJobTemplate> jobTemplateList = jobTemplateMapper.selectByExample(example);

		if (jobTemplateList != null && jobTemplateList.size() > 0) {
			PmsJobTemplate jobTemplate = jobTemplateList.get(0);

			return jobTemplate;
		}

		return null;
	}
}
