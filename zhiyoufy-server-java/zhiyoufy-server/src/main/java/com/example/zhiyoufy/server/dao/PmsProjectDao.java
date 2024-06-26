package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.bo.pms.PmsProjectDaoQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectBase;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;

public interface PmsProjectDao {
	List<PmsProjectFull> getProjectListByUserId(Long userId,
			PmsProjectDaoQueryParam queryParam);

	List<PmsProjectBase> getProjectBaseListByUserId(Long userId);
}
