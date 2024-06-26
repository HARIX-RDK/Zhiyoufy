package com.example.zhiyoufy.server.domain.dto.elkagg;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class AggTermRsp {
    int total;
    int totalPassed;
    int totalNotPassed;
    List<BucketInfo> buckets;
}
