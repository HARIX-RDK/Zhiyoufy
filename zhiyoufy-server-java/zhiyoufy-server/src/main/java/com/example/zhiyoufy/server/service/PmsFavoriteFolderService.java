package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.mbg.model.PmsFavoriteFolder;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelation;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderUpdateParam;

public interface PmsFavoriteFolderService {
	PmsFavoriteFolder addFavoriteFolder(PmsFavoriteFolderParam favoriteFolderParam);
	DeleteInfo delFavoriteFolderById(Long favoriteFolderId);
	PmsFavoriteFolder getFavoriteFolderById(Long favoriteFolderId);
	List<PmsFavoriteFolder> getFavoriteFolderList(Long projectId);
	UpdateInfo updateFavoriteFolder(Long id, PmsFavoriteFolderUpdateParam updateParam);

	int addFavoriteFolderTemplateRelation(PmsFavoriteFolderTemplateRelation relation);
	int delFavoriteFolderTemplateRelation(PmsFavoriteFolderTemplateRelation relation);
	List<PmsJobTemplate> getJobTemplateListByFolderId(Long folderId);
	List<PmsFavoriteFolder> getFavoriteFolderListByTemplateId(Long templateId);
}
