package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionUpdateParam;

public interface EmsConfigCollectionService {
	EmsConfigCollection addConfigCollection(EmsConfigCollectionParam configCollectionParam);

	int delConfigCollectionById(Long configCollectionId);
	int delConfigCollectionsByEnvId(Long envId);

	EmsConfigCollection getConfigCollectionById(Long configCollectionId);
	EmsConfigCollection getConfigCollectionByEnvIdAndName(Long envId, String name);

	CommonPage<EmsConfigCollection> getConfigCollectionList(Long envId,
			EmsConfigCollectionQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<EmsConfigCollection> getConfigCollectionList(List<Long> envIdList,
			List<String> collectionNameList);

	int updateConfigCollection(Long id, EmsConfigCollectionUpdateParam updateParam);
}
