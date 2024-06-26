package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.bo.wms.WmsWorkerAppDaoQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;

public interface WmsWorkerAppDao {
	List<WmsWorkerAppFull> getWorkerAppListByUserId(Long userId,
			WmsWorkerAppDaoQueryParam queryParam);

	List<WmsWorkerAppBase> getWorkerAppBaseListByUserId(Long userId);
}
