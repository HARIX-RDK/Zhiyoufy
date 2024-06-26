package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsGroupTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WmsGroupTokenMapper {
    long countByExample(WmsGroupTokenExample example);

    int deleteByExample(WmsGroupTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WmsGroupToken record);

    int insertSelective(WmsGroupToken record);

    List<WmsGroupToken> selectByExample(WmsGroupTokenExample example);

    WmsGroupToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WmsGroupToken record, @Param("example") WmsGroupTokenExample example);

    int updateByExample(@Param("record") WmsGroupToken record, @Param("example") WmsGroupTokenExample example);

    int updateByPrimaryKeySelective(WmsGroupToken record);

    int updateByPrimaryKey(WmsGroupToken record);
}