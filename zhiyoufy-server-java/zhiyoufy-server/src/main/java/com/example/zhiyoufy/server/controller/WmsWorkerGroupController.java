package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupUpdateParam;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_GROUP;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_GROUP_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_GROUP_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_GROUP_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_GROUP_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_GROUP_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_GROUP_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/worker-group")
@Slf4j
public class WmsWorkerGroupController {
	@Autowired
	WmsWorkerGroupService workerGroupService;

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_GROUP_ADD,
			tags = {ZHIYOUFY_WMS_WORKER_GROUP, ZHIYOUFY_WMS_WORKER_GROUP_WRITE})
	@RequestMapping(value = "/add-worker-group", method = RequestMethod.POST)
	public CommonResult<WmsWorkerGroup> addWorkerGroup(
			@Validated @RequestBody WmsWorkerGroupParam workerGroupParam) {
		WmsWorkerGroup wmsWorkerGroup =
				workerGroupService.addWorkerGroup(workerGroupParam);

		return CommonResult.success(wmsWorkerGroup);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_GROUP_DEL,
			tags = {ZHIYOUFY_WMS_WORKER_GROUP, ZHIYOUFY_WMS_WORKER_GROUP_WRITE})
	@RequestMapping(value = "/del-worker-group/{workerGroupId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delWorkerGroup(@PathVariable Long workerGroupId) {
		DeleteInfo deleteInfo = workerGroupService.delWorkerGroupById(workerGroupId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_GROUP_GET_LIST,
			tags = {ZHIYOUFY_WMS_WORKER_GROUP, ZHIYOUFY_WMS_WORKER_GROUP_READ})
	@RequestMapping(value = "/worker-group-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<WmsWorkerGroup>> getWorkerGroupList(
			WmsWorkerGroupQueryParam queryParam,
			@RequestParam(value = "workerAppId") Long workerAppId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<WmsWorkerGroup> workerGroupPage =
				workerGroupService.getWorkerGroupList(
						workerAppId, queryParam, pageSize, pageNum);

		return CommonResult.success(workerGroupPage);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_GROUP_UPDATE,
			tags = {ZHIYOUFY_WMS_WORKER_GROUP, ZHIYOUFY_WMS_WORKER_GROUP_WRITE})
	@RequestMapping(value = "/update-worker-group/{workerGroupId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateWorkerGroup(@PathVariable Long workerGroupId,
			@RequestBody WmsWorkerGroupUpdateParam updateParam) {
		UpdateInfo updateInfo = workerGroupService.updateWorkerGroup(
				workerGroupId, updateParam);

		return CommonResult.success(updateInfo);
	}
}
