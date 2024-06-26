package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsEnvironmentListUserRelation;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentListUserRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentListUserRelationMapper {
    long countByExample(EmsEnvironmentListUserRelationExample example);

    int deleteByExample(EmsEnvironmentListUserRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEnvironmentListUserRelation record);

    int insertSelective(EmsEnvironmentListUserRelation record);

    List<EmsEnvironmentListUserRelation> selectByExample(EmsEnvironmentListUserRelationExample example);

    EmsEnvironmentListUserRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEnvironmentListUserRelation record, @Param("example") EmsEnvironmentListUserRelationExample example);

    int updateByExample(@Param("record") EmsEnvironmentListUserRelation record, @Param("example") EmsEnvironmentListUserRelationExample example);

    int updateByPrimaryKeySelective(EmsEnvironmentListUserRelation record);

    int updateByPrimaryKey(EmsEnvironmentListUserRelation record);
}