package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JmsJobScheduleStructMapper {
    JmsJobScheduleStructMapper INSTANCE = Mappers.getMapper(JmsJobScheduleStructMapper.class);

    JmsJobSchedule jmsJobScheduleParamToJmsJobSchedule(JmsJobScheduleParam jobScheduleParam);
    JmsJobSchedule jmsJobScheduleUpdateParamToJmsJobSchedule(JmsJobScheduleUpdateParam updateParam);
    JmsJobSchedule jmsJobScheduleToJmsJobSchedule(JmsJobSchedule jmsJobSchedule);
}
