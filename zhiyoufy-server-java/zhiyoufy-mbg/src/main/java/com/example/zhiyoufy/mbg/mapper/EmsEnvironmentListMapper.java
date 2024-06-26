package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsEnvironmentList;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentListExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentListMapper {
    long countByExample(EmsEnvironmentListExample example);

    int deleteByExample(EmsEnvironmentListExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEnvironmentList record);

    int insertSelective(EmsEnvironmentList record);

    List<EmsEnvironmentList> selectByExample(EmsEnvironmentListExample example);

    EmsEnvironmentList selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEnvironmentList record, @Param("example") EmsEnvironmentListExample example);

    int updateByExample(@Param("record") EmsEnvironmentList record, @Param("example") EmsEnvironmentListExample example);

    int updateByPrimaryKeySelective(EmsEnvironmentList record);

    int updateByPrimaryKey(EmsEnvironmentList record);
}