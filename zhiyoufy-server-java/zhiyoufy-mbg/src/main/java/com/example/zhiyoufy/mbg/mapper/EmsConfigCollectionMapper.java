package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigCollectionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsConfigCollectionMapper {
    long countByExample(EmsConfigCollectionExample example);

    int deleteByExample(EmsConfigCollectionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsConfigCollection record);

    int insertSelective(EmsConfigCollection record);

    List<EmsConfigCollection> selectByExample(EmsConfigCollectionExample example);

    EmsConfigCollection selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsConfigCollection record, @Param("example") EmsConfigCollectionExample example);

    int updateByExample(@Param("record") EmsConfigCollection record, @Param("example") EmsConfigCollectionExample example);

    int updateByPrimaryKeySelective(EmsConfigCollection record);

    int updateByPrimaryKey(EmsConfigCollection record);
}