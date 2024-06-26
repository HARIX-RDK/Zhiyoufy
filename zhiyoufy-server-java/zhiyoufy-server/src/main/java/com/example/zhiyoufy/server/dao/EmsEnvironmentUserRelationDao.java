package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentUserRelationDao {
	List<EmsEnvironmentUserRelationFull> getUserRelationListByEnvId(
			@Param("envId") Long envId);
}
