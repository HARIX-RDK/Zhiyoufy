package com.example.zhiyoufy.server.domain.dto.elkagg;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AggDateHistogramReq {
    String guid;
    String startTime;
    String endTime;
    String interval;
    String timeZone;
    String format;
    String projectName;
    String environmentName;
    String templateName;
    String runTag;
}
