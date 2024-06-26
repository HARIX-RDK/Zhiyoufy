package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.mbg.model.PmsFavoriteFolder;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import org.apache.ibatis.annotations.Param;

public interface PmsFavoriteFolderDao {
	List<PmsJobTemplate> getJobTemplateListByFolderId(@Param("folderId") Long folderId);
	List<PmsFavoriteFolder> getFavoriteFolderListByTemplateId(
			@Param("userId") Long userId, @Param("templateId") Long templateId);
}
