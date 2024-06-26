package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationUpdateParam;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_APP;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_APP_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_WMS_WORKER_APP_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_ADD_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_DEL_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_GET_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_GET_USER_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_UPDATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_WMS_WORKER_APP_UPDATE_USER;

@RestController
@RequestMapping("/zhiyoufy-api/v1/worker-app")
@Slf4j
public class WmsWorkerAppController {
	@Autowired
	WmsWorkerAppService workerAppService;

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_ADD,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/add-worker-app", method = RequestMethod.POST)
	public CommonResult<WmsWorkerApp> addWorkerApp(
			@Validated @RequestBody WmsWorkerAppParam workerAppParam) {
		WmsWorkerApp wmsWorkerApp = workerAppService.addWorkerApp(workerAppParam);

		return CommonResult.success(wmsWorkerApp);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_DEL,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/del-worker-app/{workerAppId}", method = RequestMethod.DELETE)
	public CommonResult<DeleteInfo> delWorkerApp(@PathVariable Long workerAppId) {
		DeleteInfo deleteInfo = workerAppService.delWorkerAppById(workerAppId);

		return CommonResult.success(deleteInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_GET_BASE_LIST,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_READ})
	@RequestMapping(value = "/worker-app-base-list", method = RequestMethod.GET)
	public CommonResult<List<WmsWorkerAppBase>> getWorkerAppBaseList() {
		List<WmsWorkerAppBase> workerAppBaseList =
				workerAppService.getWorkerAppBaseList();

		return CommonResult.success(workerAppBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_GET_LIST,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_READ})
	@RequestMapping(value = "/worker-app-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<WmsWorkerAppFull>> getWorkerAppList(
			WmsWorkerAppQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<WmsWorkerAppFull> workerAppPage =
				workerAppService.getWorkerAppList(queryParam, pageSize, pageNum);

		return CommonResult.success(workerAppPage);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_UPDATE,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/update-worker-app/{workerAppId}", method = RequestMethod.POST)
	public CommonResult<UpdateInfo> updateWorkerApp(@PathVariable Long workerAppId,
			@RequestBody WmsWorkerAppUpdateParam updateParam) {
		UpdateInfo updateInfo = workerAppService.updateWorkerApp(workerAppId, updateParam);

		return CommonResult.success(updateInfo);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_ADD_USER,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/add-worker-app-user", method = RequestMethod.POST)
	public CommonResult<Integer> addWorkerAppUser(
			@RequestBody WmsWorkerAppUserRelationFull relationFull) {
		int cnt = workerAppService.addWorkerAppUserRelation(relationFull);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_DEL_USER,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/del-worker-app-user/{relationId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delWorkerAppUser(@PathVariable Long relationId) {
		Integer cnt = workerAppService.delWorkerAppUserRelationById(relationId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_GET_USER_LIST,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_READ})
	@RequestMapping(value = "/worker-app-user-list/{workerAppId}", method = RequestMethod.GET)
	public CommonResult<CommonPage<WmsWorkerAppUserRelationFull>> getWorkerAppUserList(
			@PathVariable Long workerAppId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<WmsWorkerAppUserRelationFull> relationPage =
				workerAppService.getWorkerAppUserRelationListByWorkerAppId(
						workerAppId, pageSize, pageNum);

		return CommonResult.success(relationPage);
	}

	@ElkRecordable(type = ZHIYOUFY_WMS_WORKER_APP_UPDATE_USER,
			tags = {ZHIYOUFY_WMS_WORKER_APP, ZHIYOUFY_WMS_WORKER_APP_WRITE})
	@RequestMapping(value = "/update-worker-app-user/{relationId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateWorkerAppUser(@PathVariable Long relationId,
			@RequestBody WmsWorkerAppUserRelationUpdateParam updateParam) {
		Integer cnt = workerAppService.updateWorkerAppUserRelation(relationId, updateParam);

		return CommonResult.success(cnt);
	}
}
