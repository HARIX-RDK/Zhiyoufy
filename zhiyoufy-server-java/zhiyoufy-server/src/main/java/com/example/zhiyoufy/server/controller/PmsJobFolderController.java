package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderUpdateParam;
import com.example.zhiyoufy.server.service.PmsJobFolderService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_FOLDER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_FOLDER_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_FOLDER_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_FOLDER_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_FOLDER_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_FOLDER_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_FOLDER_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/job-folder")
@Slf4j
public class PmsJobFolderController {
	@Autowired
	PmsJobFolderService jobFolderService;

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_FOLDER_ADD,
			tags = {ZHIYOUFY_PMS_JOB_FOLDER, ZHIYOUFY_PMS_JOB_FOLDER_WRITE})
	@RequestMapping(value = "/add-job-folder", method = RequestMethod.POST)
	public CommonResult<PmsJobFolder> addJobFolder(
			@Validated @RequestBody PmsJobFolderParam jobFolderParam) {
		PmsJobFolder pmsJobFolder = jobFolderService.addJobFolder(jobFolderParam);

		return CommonResult.success(pmsJobFolder);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_FOLDER_DEL,
			tags = {ZHIYOUFY_PMS_JOB_FOLDER, ZHIYOUFY_PMS_JOB_FOLDER_WRITE})
	@RequestMapping(value = "/del-job-folder/{jobFolderId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delJobFolder(@PathVariable Long jobFolderId) {
		DeleteInfo deleteInfo = jobFolderService.delJobFolderById(jobFolderId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_FOLDER_GET_LIST,
			tags = {ZHIYOUFY_PMS_JOB_FOLDER, ZHIYOUFY_PMS_JOB_FOLDER_READ})
	@RequestMapping(value = "/job-folder-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<PmsJobFolder>> getJobFolderList(
			PmsJobFolderQueryParam queryParam,
			@RequestParam(value = "projectId") Long projectId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<PmsJobFolder> jobFolderPage =
				jobFolderService.getJobFolderList(projectId, queryParam, pageSize, pageNum);

		return CommonResult.success(jobFolderPage);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_FOLDER_UPDATE,
			tags = {ZHIYOUFY_PMS_JOB_FOLDER, ZHIYOUFY_PMS_JOB_FOLDER_WRITE})
	@RequestMapping(value = "/update-job-folder/{jobFolderId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateJobFolder(@PathVariable Long jobFolderId,
			@RequestBody PmsJobFolderUpdateParam updateParam) {
		UpdateInfo updateInfo = jobFolderService.updateJobFolder(jobFolderId, updateParam);

		return CommonResult.success(updateInfo);
	}
}
