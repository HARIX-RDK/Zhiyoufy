package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import org.apache.ibatis.annotations.Param;

public interface WmsWorkerAppUserRelationDao {
	List<WmsWorkerAppUserRelationFull> getUserRelationListByWorkerAppId(
			@Param("workerAppId") Long workerAppId);
}
