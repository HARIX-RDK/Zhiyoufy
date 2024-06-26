package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.mbg.mapper.PmsFavoriteFolderMapper;
import com.example.zhiyoufy.mbg.mapper.PmsFavoriteFolderTemplateRelationMapper;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolder;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderExample;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelation;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelationExample;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.dao.PmsFavoriteFolderDao;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderUpdateParam;
import com.example.zhiyoufy.server.service.PmsFavoriteFolderService;
import com.example.zhiyoufy.server.service.PmsProjectService;
import com.example.zhiyoufy.server.service.UmsUserService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Getter
@Setter
@Slf4j
public class PmsFavoriteFolderServiceImpl implements PmsFavoriteFolderService {
	@Autowired
	PmsProjectService projectService;
	@Autowired
	UmsUserService userService;

	@Autowired
	PmsFavoriteFolderMapper favoriteFolderMapper;
	@Autowired
	PmsFavoriteFolderTemplateRelationMapper favoriteFolderTemplateRelationMapper;
	@Autowired
	PmsFavoriteFolderDao favoriteFolderDao;

	@Override
	public PmsFavoriteFolder addFavoriteFolder(PmsFavoriteFolderParam favoriteFolderParam) {
		Assert.notNull(favoriteFolderParam, "favoriteFolderParam is null");
		Assert.notNull(favoriteFolderParam.getProjectId(), "projectId is null");
		Assert.hasText(favoriteFolderParam.getName(), "name is empty");

		Long projectId = favoriteFolderParam.getProjectId();

		PmsProject pmsProject = projectService.getProjectById(projectId);

		if (pmsProject == null ||
				!pmsProject.getName().equals(favoriteFolderParam.getProjectName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		PmsProjectUserRelation relation = projectService
				.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

		if (relation == null) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		// add PmsFavoriteFolder
		PmsFavoriteFolder favoriteFolder = new PmsFavoriteFolder();
		favoriteFolder.setProjectId(projectId);
		favoriteFolder.setUserId(userDetails.getUserId());
		favoriteFolder.setName(favoriteFolderParam.getName());

		favoriteFolderMapper.insertSelective(favoriteFolder);

		return favoriteFolder;
	}

	@Override
	public DeleteInfo delFavoriteFolderById(Long favoriteFolderId) {
		PmsFavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);

		if (favoriteFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			if (!favoriteFolder.getUserId().equals(userDetails.getUserId())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		int deleted = favoriteFolderMapper.deleteByPrimaryKey(favoriteFolderId);

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(favoriteFolder.getName())
				.projectId(favoriteFolder.getProjectId())
				.build();

		return deleteInfo;
	}

	@Override
	public PmsFavoriteFolder getFavoriteFolderById(Long favoriteFolderId) {
		PmsFavoriteFolder favoriteFolder = favoriteFolderMapper.selectByPrimaryKey(favoriteFolderId);

		return favoriteFolder;
	}

	@Override
	public List<PmsFavoriteFolder> getFavoriteFolderList(Long projectId) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			PmsProjectUserRelation relation = projectService
					.getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsFavoriteFolderExample example = new PmsFavoriteFolderExample();
		PmsFavoriteFolderExample.Criteria criteria = example.createCriteria();

		criteria.andProjectIdEqualTo(projectId);

		List<PmsFavoriteFolder> favoriteFolderList = favoriteFolderMapper.selectByExample(example);

		return favoriteFolderList;
	}

	@Override
	public UpdateInfo updateFavoriteFolder(Long id, PmsFavoriteFolderUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		PmsFavoriteFolder targetFavoriteFolder = getFavoriteFolderById(id);

		if (targetFavoriteFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			if (!targetFavoriteFolder.getUserId().equals(userDetails.getUserId())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsFavoriteFolder favoriteFolder = new PmsFavoriteFolder();
		favoriteFolder.setId(id);
		favoriteFolder.setName(updateParam.getName());

		int updated = favoriteFolderMapper.updateByPrimaryKeySelective(favoriteFolder);

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetFavoriteFolder.getName())
				.projectId(targetFavoriteFolder.getProjectId())
				.build();

		return updateInfo;
	}

	@Override
	public int addFavoriteFolderTemplateRelation(PmsFavoriteFolderTemplateRelation relation) {
		Long folderId = relation.getFolderId();

		PmsFavoriteFolder favoriteFolder = getFavoriteFolderById(folderId);

		if (favoriteFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			if (!favoriteFolder.getUserId().equals(userDetails.getUserId())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		relation.setUserId(userDetails.getUserId());

		int cnt = favoriteFolderTemplateRelationMapper.insert(relation);

		return cnt;
	}

	@Override
	public int delFavoriteFolderTemplateRelation(PmsFavoriteFolderTemplateRelation relation) {
		Long folderId = relation.getFolderId();

		PmsFavoriteFolder favoriteFolder = getFavoriteFolderById(folderId);

		if (favoriteFolder == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			if (!favoriteFolder.getUserId().equals(userDetails.getUserId())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		PmsFavoriteFolderTemplateRelationExample example =
				new PmsFavoriteFolderTemplateRelationExample();
		PmsFavoriteFolderTemplateRelationExample.Criteria criteria = example.createCriteria();

		criteria.andFolderIdEqualTo(folderId)
				.andTemplateIdEqualTo(relation.getTemplateId());

		int cnt = favoriteFolderTemplateRelationMapper.deleteByExample(example);

		return cnt;
	}

	@Override
	public List<PmsJobTemplate> getJobTemplateListByFolderId(Long folderId) {
		return favoriteFolderDao.getJobTemplateListByFolderId(folderId);
	}

	@Override
	public List<PmsFavoriteFolder> getFavoriteFolderListByTemplateId(Long templateId) {
		UmsUserDetails userDetails = userService.getUserDetails();

		return favoriteFolderDao.getFavoriteFolderListByTemplateId(
				userDetails.getUserId(), templateId);
	}
}
