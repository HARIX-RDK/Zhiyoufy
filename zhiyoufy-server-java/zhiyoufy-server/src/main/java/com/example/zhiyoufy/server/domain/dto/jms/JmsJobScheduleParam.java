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
public class JmsJobScheduleParam {

    @NotEmpty
    private String name;

    @NotNull
    private long projectId;
    @NotEmpty
    private String projectName;

    @NotNull
    private Long workerAppId;
    @NotEmpty
    private String workerAppName;

    @NotNull
    private Long workerGroupId;
    @NotEmpty
    private String workerGroupName;

    @NotNull
    private long environmentId;
    @NotEmpty
    private String environmentName;

    @NotNull
    private long templateId;
    @NotEmpty
    private String templateName;

    @NotEmpty
    private String runTag;

    @Min(1)
    private Integer runNum;
    @Min(1)
    private Integer parallelNum;

    private String includeTags;
    private String excludeTags;
    private String addTags;
    private String removeTags;

    @NotEmpty
    private String crontabConfig;

}
