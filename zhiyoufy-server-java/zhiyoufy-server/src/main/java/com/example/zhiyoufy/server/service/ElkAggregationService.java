package com.example.zhiyoufy.server.service;


import com.example.zhiyoufy.server.domain.dto.elkagg.AggDateHistogramReq;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggDateHistogramRsp;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggTermReq;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggTermRsp;

public interface ElkAggregationService {
    AggDateHistogramRsp createDateHistogram(AggDateHistogramReq dateHistogramReq);
    AggTermRsp createTerm(AggTermReq termReq);

}
