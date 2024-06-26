package com.example.zhiyoufy.server.manager.scheduledjobrun.event;

import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AddScheduleJobEvent {
   JmsJobSchedule jmsJobSchedule;
}
