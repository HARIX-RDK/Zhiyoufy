package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationUpdateParam;

public interface WmsWorkerAppService {
	WmsWorkerApp addWorkerApp(WmsWorkerAppParam workerAppParam);

	DeleteInfo delWorkerAppById(Long workerAppId);

	WmsWorkerApp getWorkerAppById(Long workerAppId);

	WmsWorkerApp getWorkerAppByName(String name);

	CommonPage<WmsWorkerAppFull> getWorkerAppList(WmsWorkerAppQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<WmsWorkerAppBase> getWorkerAppBaseList();

	UpdateInfo updateWorkerApp(Long id, WmsWorkerAppUpdateParam updateParam);

	WmsWorkerAppUserRelation getWorkerAppUserRelationByWorkerAppIdAndUserId(
			Long workerAppId, Long userId);

	int addWorkerAppUserRelation(WmsWorkerAppUserRelationFull relationFull);
	int delWorkerAppUserRelationById(Long id);
	CommonPage<WmsWorkerAppUserRelationFull> getWorkerAppUserRelationListByWorkerAppId(
			Long workerAppId, Integer pageSize, Integer pageNum);
	int updateWorkerAppUserRelation(Long id,
			WmsWorkerAppUserRelationUpdateParam updateParam);
}
