package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolder;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelation;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsFavoriteFolderUpdateParam;
import com.example.zhiyoufy.server.service.PmsFavoriteFolderService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_FAVORITE_FOLDER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_FAVORITE_FOLDER_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_ADD_TEMPLATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_DEL_TEMPLATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_FOLDER_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_TEMPLATE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_FAVORITE_FOLDER_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/favorite-folder")
@Slf4j
public class PmsFavoriteFolderController {
	@Autowired
	PmsFavoriteFolderService favoriteFolderService;

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_ADD,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE})
	@RequestMapping(value = "/add-favorite-folder", method = RequestMethod.POST)
	public CommonResult<PmsFavoriteFolder> addFavoriteFolder(
			@Validated @RequestBody PmsFavoriteFolderParam favoriteFolderParam) {
		PmsFavoriteFolder pmsFavoriteFolder = favoriteFolderService.addFavoriteFolder(favoriteFolderParam);

		return CommonResult.success(pmsFavoriteFolder);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_DEL,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE})
	@RequestMapping(value = "/del-favorite-folder/{favoriteFolderId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delFavoriteFolder(@PathVariable Long favoriteFolderId) {
		DeleteInfo deleteInfo = favoriteFolderService.delFavoriteFolderById(favoriteFolderId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_LIST,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_READ})
	@RequestMapping(value = "/favorite-folder-list", method = RequestMethod.GET)
	public CommonResult<List<PmsFavoriteFolder>> getFavoriteFolderList(
			@RequestParam(value = "projectId") Long projectId) {
		List<PmsFavoriteFolder> favoriteFolderPage =
				favoriteFolderService.getFavoriteFolderList(projectId);

		return CommonResult.success(favoriteFolderPage);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_UPDATE,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE})
	@RequestMapping(value = "/update-favorite-folder/{favoriteFolderId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateFavoriteFolder(@PathVariable Long favoriteFolderId,
			@RequestBody PmsFavoriteFolderUpdateParam updateParam) {
		UpdateInfo updateInfo = favoriteFolderService.updateFavoriteFolder(favoriteFolderId, updateParam);

		return CommonResult.success(updateInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_ADD_TEMPLATE,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE})
	@RequestMapping(value = "/add-folder-template", method = RequestMethod.POST)
	public CommonResult<Integer> addFolderTemplate(
			@RequestBody PmsFavoriteFolderTemplateRelation relation) {
		int cnt = favoriteFolderService.addFavoriteFolderTemplateRelation(relation);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_DEL_TEMPLATE,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_WRITE})
	@RequestMapping(value = "/del-folder-template", method = RequestMethod.POST)
	public CommonResult<Integer> delFolderTemplate(
			@RequestBody PmsFavoriteFolderTemplateRelation relation) {
		Integer cnt = favoriteFolderService.delFavoriteFolderTemplateRelation(relation);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_TEMPLATE_LIST,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_READ})
	@RequestMapping(value = "/folder-template-list/{folderId}", method = RequestMethod.GET)
	public CommonResult<List<PmsJobTemplate>> getFolderTemplateList(
			@PathVariable Long folderId) {
		List<PmsJobTemplate> templateList =
				favoriteFolderService.getJobTemplateListByFolderId(folderId);

		return CommonResult.success(templateList);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_FAVORITE_FOLDER_GET_FOLDER_LIST,
			tags = {ZHIYOUFY_PMS_FAVORITE_FOLDER, ZHIYOUFY_PMS_FAVORITE_FOLDER_READ})
	@RequestMapping(value = "/template-folder-list/{templateId}", method = RequestMethod.GET)
	public CommonResult<List<PmsFavoriteFolder>> getTemplateFolderList(
			@PathVariable Long templateId) {
		List<PmsFavoriteFolder> folderList =
				favoriteFolderService.getFavoriteFolderListByTemplateId(templateId);

		return CommonResult.success(folderList);
	}
}
