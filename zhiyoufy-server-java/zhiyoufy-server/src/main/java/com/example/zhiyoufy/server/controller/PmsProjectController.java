package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectBase;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationUpdateParam;
import com.example.zhiyoufy.server.service.PmsProjectService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_PROJECT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_PROJECT_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_PMS_PROJECT_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_ADD_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_DEL_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_GET_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_GET_USER_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_UPDATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_PMS_PROJECT_UPDATE_USER;

@RestController
@RequestMapping("/zhiyoufy-api/v1/project")
@Slf4j
public class PmsProjectController {
	@Autowired
	PmsProjectService projectService;

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_ADD,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/add-project", method = RequestMethod.POST)
	public CommonResult<PmsProject> addProject(
			@Validated @RequestBody PmsProjectParam projectParam) {
		PmsProject pmsProject = projectService.addProject(projectParam);

		return CommonResult.success(pmsProject);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_DEL,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/del-project/{projectId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delProject(@PathVariable Long projectId) {
		Integer cnt = projectService.delProjectById(projectId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_GET_BASE_LIST,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_READ})
	@RequestMapping(value = "/project-base-list", method = RequestMethod.GET)
	public CommonResult<List<PmsProjectBase>> getProjectBaseList() {
		List<PmsProjectBase> projectBaseList =
				projectService.getProjectBaseList();

		return CommonResult.success(projectBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_GET_LIST,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_READ})
	@RequestMapping(value = "/project-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<PmsProjectFull>> getProjectList(
			PmsProjectQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<PmsProjectFull> projectPage =
				projectService.getProjectList(queryParam, pageSize, pageNum);

		return CommonResult.success(projectPage);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_UPDATE,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/update-project/{projectId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateProject(@PathVariable Long projectId,
			@RequestBody PmsProjectUpdateParam updateParam) {
		Integer cnt = projectService.updateProject(projectId, updateParam);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_ADD_USER,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/add-project-user", method = RequestMethod.POST)
	public CommonResult<Integer> addProjectUser(
			@RequestBody PmsProjectUserRelationFull relationFull) {
		int cnt = projectService.addProjectUserRelation(relationFull);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_DEL_USER,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/del-project-user/{relationId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delProjectUser(@PathVariable Long relationId) {
		Integer cnt = projectService.delProjectUserRelationById(relationId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_GET_USER_LIST,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_READ})
	@RequestMapping(value = "/project-user-list/{projectId}", method = RequestMethod.GET)
	public CommonResult<CommonPage<PmsProjectUserRelationFull>> getProjectUserList(
			@PathVariable Long projectId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<PmsProjectUserRelationFull> relationPage =
				projectService.getProjectUserRelationListByProjectId(
						projectId, pageSize, pageNum);

		return CommonResult.success(relationPage);
	}

	@ElkRecordable(type = ZHIYOUFY_PMS_PROJECT_UPDATE_USER,
			tags = {ZHIYOUFY_PMS_PROJECT, ZHIYOUFY_PMS_PROJECT_WRITE})
	@RequestMapping(value = "/update-project-user/{relationId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateProjectUser(@PathVariable Long relationId,
			@RequestBody PmsProjectUserRelationUpdateParam updateParam) {
		Integer cnt = projectService.updateProjectUserRelation(relationId, updateParam);

		return CommonResult.success(cnt);
	}
}
