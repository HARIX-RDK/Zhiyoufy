package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentBase;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationUpdateParam;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_ENVIRONMENT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_ENVIRONMENT_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_ENVIRONMENT_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_ADD_ENVIRONMENT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_ADD_ENVIRONMENT_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_DEL_ENVIRONMENT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_DEL_ENVIRONMENT_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_USER_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_UPDATE_ENVIRONMENT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_ENVIRONMENT_UPDATE_ENVIRONMENT_USER;

@RestController
@RequestMapping("/zhiyoufy-api/v1/environment")
@Slf4j
public class EmsEnvironmentController {
	@Autowired
	EmsEnvironmentService environmentService;

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_ADD_ENVIRONMENT,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/add-environment", method = RequestMethod.POST)
	public CommonResult<EmsEnvironment> addEnvironment(
			@Validated @RequestBody EmsEnvironmentParam environmentParam) {
		EmsEnvironment emsEnvironment = environmentService.addEnvironment(environmentParam);

		return CommonResult.success(emsEnvironment);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_DEL_ENVIRONMENT,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/del-environment/{envId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delEnvironment(@PathVariable Long envId) {
		Integer cnt = environmentService.delEnvironmentById(envId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_BASE_LIST,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_READ})
	@RequestMapping(value = "/environment-base-list", method = RequestMethod.GET)
	public CommonResult<List<EmsEnvironmentBase>> getEnvironmentBaseList() {
		List<EmsEnvironmentBase> environmentBaseList =
				environmentService.getEnvironmentBaseList();

		return CommonResult.success(environmentBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_LIST,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_READ})
	@RequestMapping(value = "/environment-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<EmsEnvironmentFull>> getEnvironmentList(
			EmsEnvironmentQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<EmsEnvironmentFull> environmentPage =
				environmentService.getEnvironmentList(queryParam, pageSize, pageNum);

		return CommonResult.success(environmentPage);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_UPDATE_ENVIRONMENT,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/update-environment/{envId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateEnvironment(@PathVariable Long envId,
			@RequestBody EmsEnvironmentUpdateParam updateParam) {
		Integer cnt = environmentService.updateEnvironment(envId, updateParam);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_ADD_ENVIRONMENT_USER,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/add-environment-user", method = RequestMethod.POST)
	public CommonResult<Integer> addEnvironmentUser(
			@RequestBody EmsEnvironmentUserRelationFull relationFull) {
		int cnt = environmentService.addEnvironmentUserRelation(relationFull);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_DEL_ENVIRONMENT_USER,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/del-environment-user/{relationId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delEnvironmentUser(@PathVariable Long relationId) {
		Integer cnt = environmentService.delEnvironmentUserRelationById(relationId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_GET_ENVIRONMENT_USER_LIST,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_READ})
	@RequestMapping(value = "/environment-user-list/{envId}", method = RequestMethod.GET)
	public CommonResult<CommonPage<EmsEnvironmentUserRelationFull>> getEnvironmentUserList(
			@PathVariable Long envId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<EmsEnvironmentUserRelationFull> relationPage =
				environmentService.getEnvironmentUserRelationListByEnvId(
						envId, pageSize, pageNum);

		return CommonResult.success(relationPage);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_ENVIRONMENT_UPDATE_ENVIRONMENT_USER,
			tags = {ZHIYOUFY_EMS_ENVIRONMENT, ZHIYOUFY_EMS_ENVIRONMENT_WRITE})
	@RequestMapping(value = "/update-environment-user/{relationId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateEnvironment(@PathVariable Long relationId,
			@RequestBody EmsEnvironmentUserRelationUpdateParam updateParam) {
		Integer cnt = environmentService.updateEnvironmentUserRelation(relationId, updateParam);

		return CommonResult.success(cnt);
	}
}
