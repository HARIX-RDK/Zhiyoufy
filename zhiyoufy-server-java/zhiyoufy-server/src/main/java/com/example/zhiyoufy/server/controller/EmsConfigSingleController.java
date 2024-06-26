package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleUpdateParam;
import com.example.zhiyoufy.server.service.EmsConfigSingleService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_SINGLE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_SINGLE_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_SINGLE_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_SINGLE_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_SINGLE_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_SINGLE_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_SINGLE_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/config-single")
@Slf4j
public class EmsConfigSingleController {
	@Autowired
	EmsConfigSingleService configSingleService;

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_SINGLE_ADD,
			tags = {ZHIYOUFY_EMS_CONFIG_SINGLE, ZHIYOUFY_EMS_CONFIG_SINGLE_WRITE})
	@RequestMapping(value = "/add-config-single", method = RequestMethod.POST)
	public CommonResult<EmsConfigSingle> addConfigSingle(
			@Validated @RequestBody EmsConfigSingleParam configSingleParam) {
		EmsConfigSingle emsConfigSingle = configSingleService.addConfigSingle(configSingleParam);

		return CommonResult.success(emsConfigSingle);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_SINGLE_DEL,
			tags = {ZHIYOUFY_EMS_CONFIG_SINGLE, ZHIYOUFY_EMS_CONFIG_SINGLE_WRITE})
	@RequestMapping(value = "/del-config-single/{configSingleId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delConfigSingle(@PathVariable Long configSingleId) {
		Integer cnt = configSingleService.delConfigSingleById(configSingleId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_SINGLE_GET_LIST,
			tags = {ZHIYOUFY_EMS_CONFIG_SINGLE, ZHIYOUFY_EMS_CONFIG_SINGLE_READ})
	@RequestMapping(value = "/config-single-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<EmsConfigSingle>> getConfigSingleList(
			EmsConfigSingleQueryParam queryParam,
			@RequestParam(value = "envId") Long envId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<EmsConfigSingle> configSinglePage =
				configSingleService.getConfigSingleList(envId, queryParam, pageSize, pageNum);

		return CommonResult.success(configSinglePage);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_SINGLE_UPDATE,
			tags = {ZHIYOUFY_EMS_CONFIG_SINGLE, ZHIYOUFY_EMS_CONFIG_SINGLE_WRITE})
	@RequestMapping(value = "/update-config-single/{configSingleId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateConfigSingle(@PathVariable Long configSingleId,
			@RequestBody EmsConfigSingleUpdateParam updateParam) {
		Integer cnt = configSingleService.updateConfigSingle(configSingleId, updateParam);

		return CommonResult.success(cnt);
	}
}
