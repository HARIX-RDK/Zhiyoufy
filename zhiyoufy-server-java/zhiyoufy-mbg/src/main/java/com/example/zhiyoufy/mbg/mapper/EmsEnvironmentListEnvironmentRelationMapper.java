package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsEnvironmentListEnvironmentRelation;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentListEnvironmentRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentListEnvironmentRelationMapper {
    long countByExample(EmsEnvironmentListEnvironmentRelationExample example);

    int deleteByExample(EmsEnvironmentListEnvironmentRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEnvironmentListEnvironmentRelation record);

    int insertSelective(EmsEnvironmentListEnvironmentRelation record);

    List<EmsEnvironmentListEnvironmentRelation> selectByExample(EmsEnvironmentListEnvironmentRelationExample example);

    EmsEnvironmentListEnvironmentRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEnvironmentListEnvironmentRelation record, @Param("example") EmsEnvironmentListEnvironmentRelationExample example);

    int updateByExample(@Param("record") EmsEnvironmentListEnvironmentRelation record, @Param("example") EmsEnvironmentListEnvironmentRelationExample example);

    int updateByPrimaryKeySelective(EmsEnvironmentListEnvironmentRelation record);

    int updateByPrimaryKey(EmsEnvironmentListEnvironmentRelation record);
}