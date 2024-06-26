package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemUpdateParam;
import com.example.zhiyoufy.server.service.EmsConfigItemService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_ITEM;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_ITEM_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_EMS_CONFIG_ITEM_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_ITEM_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_ITEM_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_ITEM_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_EMS_CONFIG_ITEM_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/config-item")
@Slf4j
public class EmsConfigItemController {
	@Autowired
	EmsConfigItemService configItemService;

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_ITEM_ADD,
			tags = {ZHIYOUFY_EMS_CONFIG_ITEM, ZHIYOUFY_EMS_CONFIG_ITEM_WRITE})
	@RequestMapping(value = "/add-config-item", method = RequestMethod.POST)
	public CommonResult<EmsConfigItem> addConfigItem(
			@Validated @RequestBody EmsConfigItemParam configItemParam) {
		EmsConfigItem emsConfigItem = configItemService.addConfigItem(configItemParam);

		return CommonResult.success(emsConfigItem);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_ITEM_DEL,
			tags = {ZHIYOUFY_EMS_CONFIG_ITEM, ZHIYOUFY_EMS_CONFIG_ITEM_WRITE})
	@RequestMapping(value = "/del-config-item/{configItemId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delConfigItem(@PathVariable Long configItemId) {
		Integer cnt = configItemService.delConfigItemById(configItemId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_ITEM_GET_LIST,
			tags = {ZHIYOUFY_EMS_CONFIG_ITEM, ZHIYOUFY_EMS_CONFIG_ITEM_READ})
	@RequestMapping(value = "/config-item-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<EmsConfigItem>> getConfigItemList(
			EmsConfigItemQueryParam queryParam,
			@RequestParam(value = "collectionId") Long collectionId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<EmsConfigItem> configItemPage =
				configItemService.getConfigItemList(collectionId, queryParam, pageSize, pageNum);

		return CommonResult.success(configItemPage);
	}

	@ElkRecordable(type = ZHIYOUFY_EMS_CONFIG_ITEM_UPDATE,
			tags = {ZHIYOUFY_EMS_CONFIG_ITEM, ZHIYOUFY_EMS_CONFIG_ITEM_WRITE})
	@RequestMapping(value = "/update-config-item/{configItemId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateConfigItem(@PathVariable Long configItemId,
			@RequestBody EmsConfigItemUpdateParam updateParam) {
		Integer cnt = configItemService.updateConfigItem(configItemId, updateParam);

		return CommonResult.success(cnt);
	}
}
