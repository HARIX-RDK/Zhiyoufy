package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.UmsUserLoginLog;
import com.example.zhiyoufy.mbg.model.UmsUserLoginLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsUserLoginLogMapper {
    long countByExample(UmsUserLoginLogExample example);

    int deleteByExample(UmsUserLoginLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsUserLoginLog record);

    int insertSelective(UmsUserLoginLog record);

    List<UmsUserLoginLog> selectByExample(UmsUserLoginLogExample example);

    UmsUserLoginLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsUserLoginLog record, @Param("example") UmsUserLoginLogExample example);

    int updateByExample(@Param("record") UmsUserLoginLog record, @Param("example") UmsUserLoginLogExample example);

    int updateByPrimaryKeySelective(UmsUserLoginLog record);

    int updateByPrimaryKey(UmsUserLoginLog record);
}