package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.mbg.model.UmsUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsUserMapper {
    long countByExample(UmsUserExample example);

    int deleteByExample(UmsUserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsUser record);

    int insertSelective(UmsUser record);

    List<UmsUser> selectByExample(UmsUserExample example);

    UmsUser selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsUser record, @Param("example") UmsUserExample example);

    int updateByExample(@Param("record") UmsUser record, @Param("example") UmsUserExample example);

    int updateByPrimaryKeySelective(UmsUser record);

    int updateByPrimaryKey(UmsUser record);
}