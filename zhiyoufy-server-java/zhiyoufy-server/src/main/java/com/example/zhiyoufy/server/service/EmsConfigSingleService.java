package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleUpdateParam;

public interface EmsConfigSingleService {
	EmsConfigSingle addConfigSingle(EmsConfigSingleParam configSingleParam);

	Integer delConfigSingleById(Long configSingleId);
	int delConfigSinglesByEnvId(Long envId);

	EmsConfigSingle getConfigSingleById(Long configSingleId);
	EmsConfigSingle getConfigSingleByEnvIdAndName(Long envId, String name);

	CommonPage<EmsConfigSingle> getConfigSingleList(Long envId,
			EmsConfigSingleQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	int updateConfigSingle(Long id, EmsConfigSingleUpdateParam updateParam);
}
