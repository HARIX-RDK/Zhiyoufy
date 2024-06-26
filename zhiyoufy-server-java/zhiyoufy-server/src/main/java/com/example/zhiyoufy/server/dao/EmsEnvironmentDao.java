package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.bo.ems.EmsEnvironmentDaoQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentBase;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;

public interface EmsEnvironmentDao {
	List<EmsEnvironmentFull> getEnvironmentListByUserId(Long userId,
			EmsEnvironmentDaoQueryParam queryParam);

	List<EmsEnvironmentBase> getEnvironmentBaseListByUserId(Long userId);
}
