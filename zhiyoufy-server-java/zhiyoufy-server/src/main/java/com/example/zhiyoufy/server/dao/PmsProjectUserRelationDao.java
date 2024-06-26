package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import org.apache.ibatis.annotations.Param;

public interface PmsProjectUserRelationDao {
	List<PmsProjectUserRelationFull> getUserRelationListByProjectId(
			@Param("projectId") Long projectId);
}
