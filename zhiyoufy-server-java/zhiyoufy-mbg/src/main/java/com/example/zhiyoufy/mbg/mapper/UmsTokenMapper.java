package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.UmsToken;
import com.example.zhiyoufy.mbg.model.UmsTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsTokenMapper {
    long countByExample(UmsTokenExample example);

    int deleteByExample(UmsTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsToken record);

    int insertSelective(UmsToken record);

    List<UmsToken> selectByExample(UmsTokenExample example);

    UmsToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsToken record, @Param("example") UmsTokenExample example);

    int updateByExample(@Param("record") UmsToken record, @Param("example") UmsTokenExample example);

    int updateByPrimaryKeySelective(UmsToken record);

    int updateByPrimaryKey(UmsToken record);
}