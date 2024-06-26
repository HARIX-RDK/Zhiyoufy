package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenUpdateParam;

public interface WmsGroupTokenService {
	WmsGroupToken addGroupToken(WmsGroupTokenParam groupTokenParam);

	DeleteInfo delGroupTokenById(Long groupTokenId);
	int delGroupTokensByWorkerGroupId(Long workerGroupId);
	int delGroupTokensByWorkerAppId(Long workerAppId);

	WmsGroupToken getGroupTokenById(Long groupTokenId);
	WmsGroupToken getGroupTokenByWorkerGroupIdAndName(Long workerGroupId, String name);

	CommonPage<WmsGroupToken> getGroupTokenList(Long workerGroupId,
			WmsGroupTokenQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	UpdateInfo updateGroupToken(Long id, WmsGroupTokenUpdateParam updateParam);
}
