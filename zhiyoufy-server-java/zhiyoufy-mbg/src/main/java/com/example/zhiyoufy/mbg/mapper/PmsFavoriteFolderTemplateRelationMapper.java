package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelation;
import com.example.zhiyoufy.mbg.model.PmsFavoriteFolderTemplateRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsFavoriteFolderTemplateRelationMapper {
    long countByExample(PmsFavoriteFolderTemplateRelationExample example);

    int deleteByExample(PmsFavoriteFolderTemplateRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsFavoriteFolderTemplateRelation record);

    int insertSelective(PmsFavoriteFolderTemplateRelation record);

    List<PmsFavoriteFolderTemplateRelation> selectByExample(PmsFavoriteFolderTemplateRelationExample example);

    PmsFavoriteFolderTemplateRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsFavoriteFolderTemplateRelation record, @Param("example") PmsFavoriteFolderTemplateRelationExample example);

    int updateByExample(@Param("record") PmsFavoriteFolderTemplateRelation record, @Param("example") PmsFavoriteFolderTemplateRelationExample example);

    int updateByPrimaryKeySelective(PmsFavoriteFolderTemplateRelation record);

    int updateByPrimaryKey(PmsFavoriteFolderTemplateRelation record);
}