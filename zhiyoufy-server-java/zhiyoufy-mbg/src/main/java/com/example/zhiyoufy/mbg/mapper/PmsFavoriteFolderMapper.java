package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsFavoriteFolder;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsFavoriteFolderMapper {
    long countByExample(PmsFavoriteFolderExample example);

    int deleteByExample(PmsFavoriteFolderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsFavoriteFolder record);

    int insertSelective(PmsFavoriteFolder record);

    List<PmsFavoriteFolder> selectByExample(PmsFavoriteFolderExample example);

    PmsFavoriteFolder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsFavoriteFolder record, @Param("example") PmsFavoriteFolderExample example);

    int updateByExample(@Param("record") PmsFavoriteFolder record, @Param("example") PmsFavoriteFolderExample example);

    int updateByPrimaryKeySelective(PmsFavoriteFolder record);

    int updateByPrimaryKey(PmsFavoriteFolder record);
}