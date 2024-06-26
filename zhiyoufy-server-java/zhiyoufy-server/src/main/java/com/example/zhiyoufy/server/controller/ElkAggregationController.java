package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;

import com.example.zhiyoufy.server.domain.dto.elkagg.AggDateHistogramReq;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggDateHistogramRsp;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggTermReq;
import com.example.zhiyoufy.server.domain.dto.elkagg.AggTermRsp;
import com.example.zhiyoufy.server.service.ElkAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_ELK_AGG;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_ELK_AGG_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_ELK_AGG_DATEHISTOGRAM;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_ELK_AGG_TERM;

@RestController
@RequestMapping("/zhiyoufy-api/v1/elk-agg")
public class ElkAggregationController {
    @Autowired
    ElkAggregationService elkAggService;

    @ElkRecordable(type = ZHIYOUFY_ELK_AGG_DATEHISTOGRAM,
            tags = {ZHIYOUFY_ELK_AGG, ZHIYOUFY_ELK_AGG_READ})
    @PostMapping(value = "/datehistogram")
    public CommonResult<AggDateHistogramRsp> createDateHistogram(@RequestBody AggDateHistogramReq dateHistogramReq) {
        AggDateHistogramRsp aggDateHistogramRsp = elkAggService.createDateHistogram(dateHistogramReq);

        return CommonResult.success(aggDateHistogramRsp);
    }

    @ElkRecordable(type = ZHIYOUFY_ELK_AGG_TERM,
            tags = {ZHIYOUFY_ELK_AGG, ZHIYOUFY_ELK_AGG_READ})
    @PostMapping(value = "/term")
    public CommonResult<AggTermRsp> createTerm(@RequestBody AggTermReq aggTermReq) {
        AggTermRsp aggTermRsp = elkAggService.createTerm(aggTermReq);

        return CommonResult.success(aggTermRsp);
    }
}
