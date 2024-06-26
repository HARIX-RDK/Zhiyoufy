package com.example.zhiyoufy.server.service;

import java.util.Date;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParamForJob;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemUpdateParam;

public interface EmsConfigItemService {
	EmsConfigItem addConfigItem(EmsConfigItemParam configItemParam);

	int delConfigItemById(Long configItemId);
	int delConfigItemsByCollectionId(Long collectionId);
	int delConfigItemsByEnvId(Long envId);

	EmsConfigItem getConfigItemById(Long configItemId);
	EmsConfigItem getConfigItemByCollectionIdAndName(Long collectionId, String name);

	CommonPage<EmsConfigItem> getConfigItemList(Long collectionId,
			EmsConfigItemQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<EmsConfigItem> getConfigItemList(Long collectionId,
			EmsConfigItemQueryParamForJob queryParam);
	List<String> getRunIdListByInUse();

	int updateConfigItem(Long id, EmsConfigItemUpdateParam updateParam);
	int updateConfigItemsForAlloc(List<Long> idList, String usageId, Date usageTimeoutAt);
	int updateConfigItemsForFreeByIdList(List<Long> idList);
	int updateConfigItemsForFreeByUsageTimeoutAt(Date usageTimeoutAt);
	int updateConfigItemsForFreeByUsageId(String usageId);
}
