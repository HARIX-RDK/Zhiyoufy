package com.example.zhiyoufy.server.domain.dto.jms;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JmsJobScheduleQueryParam {
    private Long environmentId;
    private Long projectId;
    private String templateName;
    private String runTag;
}
