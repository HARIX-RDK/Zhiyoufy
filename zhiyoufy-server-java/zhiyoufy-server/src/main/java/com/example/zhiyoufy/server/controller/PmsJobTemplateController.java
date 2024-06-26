package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateUpdateParam;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_TEMPLATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_TEMPLATE_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_JOB_TEMPLATE_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_TEMPLATE_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_TEMPLATE_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_TEMPLATE_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_JOB_TEMPLATE_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/job-template")
@Slf4j
public class PmsJobTemplateController {
	@Autowired
	PmsJobTemplateService jobTemplateService;

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_TEMPLATE_ADD,
			tags = {ZHIYOUFY_PMS_JOB_TEMPLATE, ZHIYOUFY_PMS_JOB_TEMPLATE_WRITE})
	@RequestMapping(value = "/add-job-template", method = RequestMethod.POST)
	public CommonResult<PmsJobTemplate> addJobTemplate(
			@Validated @RequestBody PmsJobTemplateParam jobTemplateParam) {
		PmsJobTemplate pmsJobTemplate = jobTemplateService.addJobTemplate(jobTemplateParam);

		return CommonResult.success(pmsJobTemplate);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_TEMPLATE_DEL,
			tags = {ZHIYOUFY_PMS_JOB_TEMPLATE, ZHIYOUFY_PMS_JOB_TEMPLATE_WRITE})
	@RequestMapping(value = "/del-job-template/{jobTemplateId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delJobTemplate(@PathVariable Long jobTemplateId) {
		DeleteInfo deleteInfo = jobTemplateService.delJobTemplateById(jobTemplateId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_TEMPLATE_GET_LIST,
			tags = {ZHIYOUFY_PMS_JOB_TEMPLATE, ZHIYOUFY_PMS_JOB_TEMPLATE_READ})
	@RequestMapping(value = "/job-template-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<PmsJobTemplate>> getJobTemplateList(
			PmsJobTemplateQueryParam queryParam,
			@RequestParam(value = "projectId") Long projectId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<PmsJobTemplate> jobTemplatePage =
				jobTemplateService.getJobTemplateList(projectId, queryParam, pageSize, pageNum);

		return CommonResult.success(jobTemplatePage);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_JOB_TEMPLATE_UPDATE,
			tags = {ZHIYOUFY_PMS_JOB_TEMPLATE, ZHIYOUFY_PMS_JOB_TEMPLATE_WRITE})
	@RequestMapping(value = "/update-job-template/{jobTemplateId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateJobTemplate(@PathVariable Long jobTemplateId,
			@RequestBody PmsJobTemplateUpdateParam updateParam) {
		UpdateInfo updateInfo = jobTemplateService.updateJobTemplate(jobTemplateId, updateParam);

		return CommonResult.success(updateInfo);
	}
}
