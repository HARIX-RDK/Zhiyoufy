package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenUpdateParam;
import com.example.zhiyoufy.server.service.WmsGroupTokenService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_GROUP_TOKEN;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_GROUP_TOKEN_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_GROUP_TOKEN_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_GROUP_TOKEN_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_GROUP_TOKEN_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_GROUP_TOKEN_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_GROUP_TOKEN_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/group-token")
@Slf4j
public class WmsGroupTokenController {
	@Autowired
	WmsGroupTokenService groupTokenService;

	@ElkRecordable(type = ZHIYOUFY_WMS_GROUP_TOKEN_ADD,
			tags = {ZHIYOUFY_WMS_GROUP_TOKEN, ZHIYOUFY_WMS_GROUP_TOKEN_WRITE})
	@RequestMapping(value = "/add-group-token", method = RequestMethod.POST)
	public CommonResult<WmsGroupToken> addGroupToken(
			@Validated @RequestBody WmsGroupTokenParam groupTokenParam) {
		WmsGroupToken wmsGroupToken = groupTokenService.addGroupToken(groupTokenParam);

		return CommonResult.success(wmsGroupToken);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_GROUP_TOKEN_DEL,
			tags = {ZHIYOUFY_WMS_GROUP_TOKEN, ZHIYOUFY_WMS_GROUP_TOKEN_WRITE})
	@RequestMapping(value = "/del-group-token/{groupTokenId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delGroupToken(@PathVariable Long groupTokenId) {
		DeleteInfo deleteInfo = groupTokenService.delGroupTokenById(groupTokenId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_GROUP_TOKEN_GET_LIST,
			tags = {ZHIYOUFY_WMS_GROUP_TOKEN, ZHIYOUFY_WMS_GROUP_TOKEN_READ})
	@RequestMapping(value = "/group-token-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<WmsGroupToken>> getGroupTokenList(
			WmsGroupTokenQueryParam queryParam,
			@RequestParam(value = "workerGroupId") Long workerGroupId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<WmsGroupToken> groupTokenPage =
				groupTokenService.getGroupTokenList(workerGroupId, queryParam, pageSize, pageNum);

		return CommonResult.success(groupTokenPage);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_GROUP_TOKEN_UPDATE,
			tags = {ZHIYOUFY_WMS_GROUP_TOKEN, ZHIYOUFY_WMS_GROUP_TOKEN_WRITE})
	@RequestMapping(value = "/update-group-token/{groupTokenId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateGroupToken(@PathVariable Long groupTokenId,
			@RequestBody WmsGroupTokenUpdateParam updateParam) {
		UpdateInfo updateInfo = groupTokenService.updateGroupToken(groupTokenId, updateParam);

		return CommonResult.success(updateInfo);
	}
}
