package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.mbg.model.JmsJobScheduleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JmsJobScheduleMapper {
    long countByExample(JmsJobScheduleExample example);

    int deleteByExample(JmsJobScheduleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(JmsJobSchedule record);

    int insertSelective(JmsJobSchedule record);

    List<JmsJobSchedule> selectByExample(JmsJobScheduleExample example);

    JmsJobSchedule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") JmsJobSchedule record, @Param("example") JmsJobScheduleExample example);

    int updateByExample(@Param("record") JmsJobSchedule record, @Param("example") JmsJobScheduleExample example);

    int updateByPrimaryKeySelective(JmsJobSchedule record);

    int updateByPrimaryKey(JmsJobSchedule record);
}