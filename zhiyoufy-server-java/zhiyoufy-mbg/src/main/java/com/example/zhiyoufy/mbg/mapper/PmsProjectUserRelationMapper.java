package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProjectUserRelationMapper {
    long countByExample(PmsProjectUserRelationExample example);

    int deleteByExample(PmsProjectUserRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProjectUserRelation record);

    int insertSelective(PmsProjectUserRelation record);

    List<PmsProjectUserRelation> selectByExample(PmsProjectUserRelationExample example);

    PmsProjectUserRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProjectUserRelation record, @Param("example") PmsProjectUserRelationExample example);

    int updateByExample(@Param("record") PmsProjectUserRelation record, @Param("example") PmsProjectUserRelationExample example);

    int updateByPrimaryKeySelective(PmsProjectUserRelation record);

    int updateByPrimaryKey(PmsProjectUserRelation record);
}