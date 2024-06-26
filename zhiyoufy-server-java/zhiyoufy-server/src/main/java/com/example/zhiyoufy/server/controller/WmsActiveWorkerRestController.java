package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.service.WmsActiveWorkerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_ACTIVE_WORKER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_ACTIVE_WORKER_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_ACTIVE_WORKER_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_ACTIVE_WORKER_DISCONNECT_SESSION;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_ACTIVE_WORKER_GET_APP_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_ACTIVE_WORKER_GET_GROUP_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_ACTIVE_WORKER_GET_WORKER_BASE_LIST;

@RestController
@RequestMapping("/zhiyoufy-api/v1/active-worker")
@Slf4j
public class WmsActiveWorkerRestController {
	@Autowired
	WmsActiveWorkerService activeWorkerService;

	@ElkRecordable(type = ZHIYOUFY_WMS_ACTIVE_WORKER_GET_APP_BASE_LIST,
			tags = {ZHIYOUFY_WMS_ACTIVE_WORKER, ZHIYOUFY_WMS_ACTIVE_WORKER_READ})
	@RequestMapping(value = "/app-base-list", method = RequestMethod.GET)
	public CommonResult<List<WmsWorkerAppBase>> getAppBaseList() {
		List<WmsWorkerAppBase> workerAppBaseList =
				activeWorkerService.getAppBaseList();

		return CommonResult.success(workerAppBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_ACTIVE_WORKER_GET_GROUP_BASE_LIST,
			tags = {ZHIYOUFY_WMS_ACTIVE_WORKER, ZHIYOUFY_WMS_ACTIVE_WORKER_READ})
	@RequestMapping(value = "/group-base-list", method = RequestMethod.GET)
	public CommonResult<List<WmsActiveWorkerGroupBase>> getGroupBaseList(
			@RequestParam(value = "workerAppId") Long workerAppId) {
		List<WmsActiveWorkerGroupBase> workerGroupBaseList =
				activeWorkerService.getGroupBaseListByAppId(workerAppId);

		return CommonResult.success(workerGroupBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_ACTIVE_WORKER_GET_WORKER_BASE_LIST,
			tags = {ZHIYOUFY_WMS_ACTIVE_WORKER, ZHIYOUFY_WMS_ACTIVE_WORKER_READ})
	@RequestMapping(value = "/worker-base-list", method = RequestMethod.GET)
	public CommonResult<List<WmsActiveWorkerBase>> getWorkerBaseList(
			@RequestParam(value = "workerGroupId") Long workerGroupId) {
		List<WmsActiveWorkerBase> workerBaseList =
				activeWorkerService.getWorkerBaseListByGroupId(workerGroupId);

		return CommonResult.success(workerBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_ACTIVE_WORKER_DISCONNECT_SESSION,
			tags = {ZHIYOUFY_WMS_ACTIVE_WORKER, ZHIYOUFY_WMS_ACTIVE_WORKER_WRITE})
	@RequestMapping(value = "/disconnect-session/{sessionId}", method = RequestMethod.POST)
	public CommonResult disconnectSession(@PathVariable String sessionId) {
		activeWorkerService.disconnectSession(sessionId);

		return CommonResult.success();
	}
}
