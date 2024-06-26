package com.example.zhiyoufy.server.domain.dto.jms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JmsJobScheduleUpdateParam {

    private String runTag;
    private Integer runNum;
    private Integer parallelNum;

    private String includeTags;
    private String excludeTags;
    private String addTags;
    private String removeTags;

    private String crontabConfig;

}

