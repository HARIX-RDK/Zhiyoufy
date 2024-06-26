package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WmsWorkerAppMapper {
    long countByExample(WmsWorkerAppExample example);

    int deleteByExample(WmsWorkerAppExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WmsWorkerApp record);

    int insertSelective(WmsWorkerApp record);

    List<WmsWorkerApp> selectByExample(WmsWorkerAppExample example);

    WmsWorkerApp selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WmsWorkerApp record, @Param("example") WmsWorkerAppExample example);

    int updateByExample(@Param("record") WmsWorkerApp record, @Param("example") WmsWorkerAppExample example);

    int updateByPrimaryKeySelective(WmsWorkerApp record);

    int updateByPrimaryKey(WmsWorkerApp record);
}