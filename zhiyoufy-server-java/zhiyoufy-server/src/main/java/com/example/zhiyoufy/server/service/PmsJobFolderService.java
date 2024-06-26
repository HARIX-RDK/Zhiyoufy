package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderUpdateParam;

public interface PmsJobFolderService {
	PmsJobFolder addJobFolder(PmsJobFolderParam jobFolderParam);

	DeleteInfo delJobFolderById(Long jobFolderId);
	int delJobFoldersByProjectId(Long projectId);

	PmsJobFolder getJobFolderById(Long jobFolderId);
	PmsJobFolder getJobFolderByProjectIdAndName(Long projectId, String name);

	CommonPage<PmsJobFolder> getJobFolderList(Long projectId,
			PmsJobFolderQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	UpdateInfo updateJobFolder(Long id, PmsJobFolderUpdateParam updateParam);
}
