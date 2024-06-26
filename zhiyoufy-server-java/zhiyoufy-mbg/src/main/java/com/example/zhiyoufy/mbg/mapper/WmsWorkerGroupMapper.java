package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WmsWorkerGroupMapper {
    long countByExample(WmsWorkerGroupExample example);

    int deleteByExample(WmsWorkerGroupExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WmsWorkerGroup record);

    int insertSelective(WmsWorkerGroup record);

    List<WmsWorkerGroup> selectByExample(WmsWorkerGroupExample example);

    WmsWorkerGroup selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WmsWorkerGroup record, @Param("example") WmsWorkerGroupExample example);

    int updateByExample(@Param("record") WmsWorkerGroup record, @Param("example") WmsWorkerGroupExample example);

    int updateByPrimaryKeySelective(WmsWorkerGroup record);

    int updateByPrimaryKey(WmsWorkerGroup record);
}