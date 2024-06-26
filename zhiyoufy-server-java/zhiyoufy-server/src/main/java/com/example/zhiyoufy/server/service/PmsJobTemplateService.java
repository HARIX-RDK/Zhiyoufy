package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateUpdateParam;

public interface PmsJobTemplateService {
	PmsJobTemplate addJobTemplate(PmsJobTemplateParam jobTemplateParam);

	DeleteInfo delJobTemplateById(Long jobTemplateId);
	int delJobTemplatesByFolderId(Long folderId);
	int delJobTemplatesByProjectId(Long projectId);

	PmsJobTemplate getJobTemplateById(Long jobTemplateId);
	PmsJobTemplate getJobTemplateByProjectIdAndName(Long projectId, String name);

	CommonPage<PmsJobTemplate> getJobTemplateList(Long projectId,
			PmsJobTemplateQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	UpdateInfo updateJobTemplate(Long id, PmsJobTemplateUpdateParam updateParam);
}
