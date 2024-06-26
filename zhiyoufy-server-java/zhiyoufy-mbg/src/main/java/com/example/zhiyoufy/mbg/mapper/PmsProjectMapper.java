package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProjectMapper {
    long countByExample(PmsProjectExample example);

    int deleteByExample(PmsProjectExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProject record);

    int insertSelective(PmsProject record);

    List<PmsProject> selectByExample(PmsProjectExample example);

    PmsProject selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProject record, @Param("example") PmsProjectExample example);

    int updateByExample(@Param("record") PmsProject record, @Param("example") PmsProjectExample example);

    int updateByPrimaryKeySelective(PmsProject record);

    int updateByPrimaryKey(PmsProject record);
}