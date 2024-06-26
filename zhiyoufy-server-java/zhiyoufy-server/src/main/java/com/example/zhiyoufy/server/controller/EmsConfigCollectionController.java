package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionUpdateParam;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_COLLECTION;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_COLLECTION_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_COLLECTION_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_COLLECTION_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_COLLECTION_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_COLLECTION_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_COLLECTION_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/config-collection")
@Slf4j
public class EmsConfigCollectionController {
	@Autowired
	EmsConfigCollectionService configCollectionService;

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_COLLECTION_ADD,
			tags = {ZHIYOUFY_EMS_CONFIG_COLLECTION, ZHIYOUFY_EMS_CONFIG_COLLECTION_WRITE})
	@RequestMapping(value = "/add-config-collection", method = RequestMethod.POST)
	public CommonResult<EmsConfigCollection> addConfigCollection(
			@Validated @RequestBody EmsConfigCollectionParam configCollectionParam) {
		EmsConfigCollection emsConfigCollection =
				configCollectionService.addConfigCollection(configCollectionParam);

		return CommonResult.success(emsConfigCollection);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_COLLECTION_DEL,
			tags = {ZHIYOUFY_EMS_CONFIG_COLLECTION, ZHIYOUFY_EMS_CONFIG_COLLECTION_WRITE})
	@RequestMapping(value = "/del-config-collection/{configCollectionId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delConfigCollection(@PathVariable Long configCollectionId) {
		Integer cnt = configCollectionService.delConfigCollectionById(configCollectionId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_COLLECTION_GET_LIST,
			tags = {ZHIYOUFY_EMS_CONFIG_COLLECTION, ZHIYOUFY_EMS_CONFIG_COLLECTION_READ})
	@RequestMapping(value = "/config-collection-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<EmsConfigCollection>> getConfigCollectionList(
			EmsConfigCollectionQueryParam queryParam,
			@RequestParam(value = "envId") Long envId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<EmsConfigCollection> configCollectionPage =
				configCollectionService.getConfigCollectionList(
						envId, queryParam, pageSize, pageNum);

		return CommonResult.success(configCollectionPage);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_COLLECTION_UPDATE,
			tags = {ZHIYOUFY_EMS_CONFIG_COLLECTION, ZHIYOUFY_EMS_CONFIG_COLLECTION_WRITE})
	@RequestMapping(value = "/update-config-collection/{configCollectionId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateConfigCollection(@PathVariable Long configCollectionId,
			@RequestBody EmsConfigCollectionUpdateParam updateParam) {
		Integer cnt = configCollectionService.updateConfigCollection(
				configCollectionId, updateParam);

		return CommonResult.success(cnt);
	}
}
