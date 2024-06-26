package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WmsWorkerAppUserRelationMapper {
    long countByExample(WmsWorkerAppUserRelationExample example);

    int deleteByExample(WmsWorkerAppUserRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WmsWorkerAppUserRelation record);

    int insertSelective(WmsWorkerAppUserRelation record);

    List<WmsWorkerAppUserRelation> selectByExample(WmsWorkerAppUserRelationExample example);

    WmsWorkerAppUserRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WmsWorkerAppUserRelation record, @Param("example") WmsWorkerAppUserRelationExample example);

    int updateByExample(@Param("record") WmsWorkerAppUserRelation record, @Param("example") WmsWorkerAppUserRelationExample example);

    int updateByPrimaryKeySelective(WmsWorkerAppUserRelation record);

    int updateByPrimaryKey(WmsWorkerAppUserRelation record);
}