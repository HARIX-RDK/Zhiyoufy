package com.example.zhiyoufy.server.manager.scheduledjobrun.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DeleteScheduleJobEvent {
    private long id;
    private String name;
}
