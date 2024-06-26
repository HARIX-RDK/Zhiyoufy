package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.PmsJobFolderMapper;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobFolderExample;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderUpdateParam;
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
public class PmsJobFolderServiceImpl implements PmsJobFolderService {
	@Autowired
	PmsProjectService projectService;
	@Autowired
	PmsJobTemplateService jobTemplateService;
	@Autowired
	UmsUserService userService;

	@Autowired
	PmsJobFolderMapper jobFolderMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	public PmsJobFolder addJobFolder(PmsJobFolderParam jobFolderParam) {
		Assert.notNull(jobFolderParam, "jobFolderParam is null");
		Assert.notNull(jobFolderParam.getProjectId(), "projectId is null");
		Assert.hasText(jobFolderParam.getName(), "name is empty");

		Long projectId = jobFolderParam.getProjectId();

		PmsJobFolder jobFolder = getJobFolderByProjectIdAndName(
				projectId, jobFolderParam.getName());

		if (jobFolder != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		PmsProject pmsProject = projectService.getProjectById(projectId);

		if (pmsProject == null ||
				!pmsProject.getName().equals(jobFolderParam.getProjectName())) {
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

		// add PmsJobFolder
		jobFolder = PmsProjectStructMapper.INSTANCE
				.pmsJobFolderParamToPmsJobFolder(jobFolderParam);
		jobFolder.setCreatedBy(userDetails.getUsername());
		jobFolder.setModifiedBy(userDetails.getUsername());

		jobFolderMapper.insertSelective(jobFolder);

		return jobFolder;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	@Transactional
	public DeleteInfo delJobFolderById(Long jobFolderId) {
		PmsJobFolder jobFolder = getJobFolderById(jobFolderId);

		if (jobFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long projectId = jobFolder.getProjectId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		int deleted = delJobFolder(jobFolder);

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(jobFolder.getName())
				.projectId(projectId)
				.build();

		return deleteInfo;
	}

	@Override
	public int delJobFoldersByProjectId(Long projectId) {
		PmsJobFolderExample example = new PmsJobFolderExample();
		PmsJobFolderExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		int deleted = jobFolderMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public PmsJobFolder getJobFolderById(Long jobFolderId) {
		PmsJobFolder jobFolder = jobFolderMapper.selectByPrimaryKey(jobFolderId);

		return jobFolder;
	}

	@Override
	public PmsJobFolder getJobFolderByProjectIdAndName(Long projectId, String name) {
		PmsJobFolder jobFolder = getJobFolderByProjectIdAndNameFromDb(projectId, name);

		return jobFolder;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor') "
			+ "|| hasAuthority('roles/project.viewer')")
	public CommonPage<PmsJobFolder> getJobFolderList(Long projectId,
			PmsJobFolderQueryParam queryParam, Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsJobFolderExample example = new PmsJobFolderExample();
		PmsJobFolderExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		if (queryParam.getParentId() != null) {
			criteria.andParentIdEqualTo(queryParam.getParentId());
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

		List<PmsJobFolder> jobFolderList = jobFolderMapper.selectByExample(example);

		return CommonPage.restPage(jobFolderList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/project.owner') "
			+ "|| hasAuthority('roles/project.editor')")
	public UpdateInfo updateJobFolder(Long id, PmsJobFolderUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		PmsJobFolder targetJobFolder = getJobFolderById(id);

		if (targetJobFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long projectId = targetJobFolder.getProjectId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsJobFolder jobFolder = PmsProjectStructMapper.INSTANCE
				.pmsJobFolderUpdateParamToPmsJobFolder(updateParam);
		jobFolder.setId(id);
		jobFolder.setModifiedBy(userDetails.getUsername());

		int updated = jobFolderMapper.updateByPrimaryKeySelective(jobFolder);

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetJobFolder.getName())
				.projectId(projectId)
				.build();

		return updateInfo;
	}

	private int delJobFolder(PmsJobFolder jobFolder) {
		Long jobFolderId = jobFolder.getId();
		Long projectId = jobFolder.getProjectId();

		// delete child folders
		List<PmsJobFolder> childJobFolderList = getJobFoldersByProjectIdAndParentId(
				projectId, jobFolderId);

		int deleted = 0;
		for (PmsJobFolder childJobFolder : childJobFolderList) {
			deleted += delJobFolder(childJobFolder);
		}

		// delete job templates in this folder
		jobTemplateService.delJobTemplatesByFolderId(jobFolderId);

		// delete PmsJobFolder
		deleted += jobFolderMapper.deleteByPrimaryKey(jobFolderId);

		return deleted;
	}

	private List<PmsJobFolder> getJobFoldersByProjectIdAndParentId(
			Long projectId, Long parentId) {
		PmsJobFolderExample example = new PmsJobFolderExample();
		PmsJobFolderExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		criteria.andParentIdEqualTo(parentId);

		List<PmsJobFolder> jobFolderList = jobFolderMapper.selectByExample(example);

		return jobFolderList;
	}

	private PmsJobFolder getJobFolderByProjectIdAndNameFromDb(Long projectId, String name) {
		PmsJobFolderExample example = new PmsJobFolderExample();
		PmsJobFolderExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId).andNameEqualTo(name);

		List<PmsJobFolder> jobFolderList = jobFolderMapper.selectByExample(example);

		if (jobFolderList != null && jobFolderList.size() > 0) {
			PmsJobFolder jobFolder = jobFolderList.get(0);

			return jobFolder;
		}

		return null;
	}
}
