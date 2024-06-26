package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupUpdateParam;

public interface WmsWorkerGroupService {
	WmsWorkerGroup addWorkerGroup(WmsWorkerGroupParam workerGroupParam);

	DeleteInfo delWorkerGroupById(Long workerGroupId);
	int delWorkerGroupsByWorkerAppId(Long workerAppId);

	WmsWorkerGroup getWorkerGroupById(Long workerGroupId);
	WmsWorkerGroup getWorkerGroupByWorkerAppIdAndName(Long workerAppId, String name);

	CommonPage<WmsWorkerGroup> getWorkerGroupList(Long workerAppId,
			WmsWorkerGroupQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<WmsWorkerGroupBase> getWorkerGroupBaseList();

	UpdateInfo updateWorkerGroup(Long id, WmsWorkerGroupUpdateParam updateParam);
}
