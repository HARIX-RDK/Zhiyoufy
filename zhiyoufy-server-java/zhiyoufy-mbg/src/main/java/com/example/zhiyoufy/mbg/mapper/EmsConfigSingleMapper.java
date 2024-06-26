package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.mbg.model.EmsConfigSingleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsConfigSingleMapper {
    long countByExample(EmsConfigSingleExample example);

    int deleteByExample(EmsConfigSingleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsConfigSingle record);

    int insertSelective(EmsConfigSingle record);

    List<EmsConfigSingle> selectByExampleWithBLOBs(EmsConfigSingleExample example);

    List<EmsConfigSingle> selectByExample(EmsConfigSingleExample example);

    EmsConfigSingle selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsConfigSingle record, @Param("example") EmsConfigSingleExample example);

    int updateByExampleWithBLOBs(@Param("record") EmsConfigSingle record, @Param("example") EmsConfigSingleExample example);

    int updateByExample(@Param("record") EmsConfigSingle record, @Param("example") EmsConfigSingleExample example);

    int updateByPrimaryKeySelective(EmsConfigSingle record);

    int updateByPrimaryKeyWithBLOBs(EmsConfigSingle record);

    int updateByPrimaryKey(EmsConfigSingle record);
}