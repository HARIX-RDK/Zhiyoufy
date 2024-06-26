package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsConfigItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsConfigItemMapper {
    long countByExample(EmsConfigItemExample example);

    int deleteByExample(EmsConfigItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsConfigItem record);

    int insertSelective(EmsConfigItem record);

    List<EmsConfigItem> selectByExampleWithBLOBs(EmsConfigItemExample example);

    List<EmsConfigItem> selectByExample(EmsConfigItemExample example);

    EmsConfigItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsConfigItem record, @Param("example") EmsConfigItemExample example);

    int updateByExampleWithBLOBs(@Param("record") EmsConfigItem record, @Param("example") EmsConfigItemExample example);

    int updateByExample(@Param("record") EmsConfigItem record, @Param("example") EmsConfigItemExample example);

    int updateByPrimaryKeySelective(EmsConfigItem record);

    int updateByPrimaryKeyWithBLOBs(EmsConfigItem record);

    int updateByPrimaryKey(EmsConfigItem record);
}