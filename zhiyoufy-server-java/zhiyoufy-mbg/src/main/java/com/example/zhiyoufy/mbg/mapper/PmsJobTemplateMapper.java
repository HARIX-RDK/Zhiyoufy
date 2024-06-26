package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsJobTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsJobTemplateMapper {
    long countByExample(PmsJobTemplateExample example);

    int deleteByExample(PmsJobTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsJobTemplate record);

    int insertSelective(PmsJobTemplate record);

    List<PmsJobTemplate> selectByExample(PmsJobTemplateExample example);

    PmsJobTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsJobTemplate record, @Param("example") PmsJobTemplateExample example);

    int updateByExample(@Param("record") PmsJobTemplate record, @Param("example") PmsJobTemplateExample example);

    int updateByPrimaryKeySelective(PmsJobTemplate record);

    int updateByPrimaryKey(PmsJobTemplate record);
}