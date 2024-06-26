package com.example.zhiyoufy.server.domain.dto.elkagg;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BucketInfo {
    String key;
    int passedCount;
    int notPassedCount;
}
