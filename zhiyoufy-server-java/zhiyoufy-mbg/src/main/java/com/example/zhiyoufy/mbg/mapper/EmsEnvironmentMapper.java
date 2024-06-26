package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEnvironmentMapper {
    long countByExample(EmsEnvironmentExample example);

    int deleteByExample(EmsEnvironmentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEnvironment record);

    int insertSelective(EmsEnvironment record);

    List<EmsEnvironment> selectByExample(EmsEnvironmentExample example);

    EmsEnvironment selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEnvironment record, @Param("example") EmsEnvironmentExample example);

    int updateByExample(@Param("record") EmsEnvironment record, @Param("example") EmsEnvironmentExample example);

    int updateByPrimaryKeySelective(EmsEnvironment record);

    int updateByPrimaryKey(EmsEnvironment record);
}