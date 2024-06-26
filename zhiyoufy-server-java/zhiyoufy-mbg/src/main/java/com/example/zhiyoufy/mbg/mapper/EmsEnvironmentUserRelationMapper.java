package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentUserRelationMapper {
    long countByExample(EmsEnvironmentUserRelationExample example);

    int deleteByExample(EmsEnvironmentUserRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEnvironmentUserRelation record);

    int insertSelective(EmsEnvironmentUserRelation record);

    List<EmsEnvironmentUserRelation> selectByExample(EmsEnvironmentUserRelationExample example);

    EmsEnvironmentUserRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEnvironmentUserRelation record, @Param("example") EmsEnvironmentUserRelationExample example);

    int updateByExample(@Param("record") EmsEnvironmentUserRelation record, @Param("example") EmsEnvironmentUserRelationExample example);

    int updateByPrimaryKeySelective(EmsEnvironmentUserRelation record);

    int updateByPrimaryKey(EmsEnvironmentUserRelation record);
}