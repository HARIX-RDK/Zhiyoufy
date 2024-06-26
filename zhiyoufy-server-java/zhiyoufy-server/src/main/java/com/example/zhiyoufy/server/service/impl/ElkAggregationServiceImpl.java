package com.example.zhiyoufy.server.service.impl;

import com.example.zhiyoufy.server.domain.dto.elkagg.*;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.service.ElkAggregationService;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.core.*;


import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
@Slf4j
public class ElkAggregationServiceImpl implements ElkAggregationService {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Override
    public AggDateHistogramRsp createDateHistogram(AggDateHistogramReq dateHistogramReq) {
        log.info("enter createDateHistogram, dateHistogramReq= {}", dateHistogramReq.toString());

        String aggName = "runs_over_term";
        ZoneId zoneId = TimeZone.getTimeZone(dateHistogramReq.getTimeZone()).toZoneId();
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(dateHistogramReq.getInterval());

        DateHistogramAggregationBuilder dateHistogramAggregation = AggregationBuilders
                .dateHistogram(aggName)
                .field("timestamp")
                .calendarInterval(dateHistogramInterval)
                .format(dateHistogramReq.getFormat())
                .timeZone(zoneId)
                .subAggregation(AggregationBuilders.terms("pass").field("passed"));

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp")
                .gte(dateHistogramReq.getStartTime())
                .lte(dateHistogramReq.getEndTime());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter().add(rangeQueryBuilder);


        if(StringUtils.hasText(dateHistogramReq.getEnvironmentName()) && !dateHistogramReq.getEnvironmentName().equals("all")) {
            TermQueryBuilder termQueryBuilder =
                    QueryBuilders.termQuery("environmentName", dateHistogramReq.getEnvironmentName());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }

        if(StringUtils.hasText(dateHistogramReq.getRunTag())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("runTag.keyword", dateHistogramReq.getRunTag());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }
        if(StringUtils.hasText(dateHistogramReq.getTemplateName())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("templateName", dateHistogramReq.getTemplateName());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }

        if(StringUtils.hasText(dateHistogramReq.getProjectName())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("projectName", dateHistogramReq.getProjectName());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }

        NativeSearchQueryBuilder nativeQueryBuilder =  new NativeSearchQueryBuilder();
        nativeQueryBuilder.withPageable(PageRequest.of(0,1))
                .withAggregations(dateHistogramAggregation)
                .withQuery(boolQueryBuilder);


        SearchHits<JmsJobRunResultFull> searchHints =
                elasticsearchOperations.search(nativeQueryBuilder.build(), JmsJobRunResultFull.class);

        List<BucketInfo> resutltBuckets = new ArrayList<>();
        int totalCount = 0;
        int totalPassedCount = 0;
        int totalNotPassedCount = 0;
        AggregationsContainer<Aggregations> aggregationsContainer =
                (AggregationsContainer<Aggregations>) searchHints.getAggregations();
        Aggregations aggregations = aggregationsContainer.aggregations();
        ParsedDateHistogram parsedDateHistogram = aggregations.get(aggName);
        List<ParsedDateHistogram.ParsedBucket> buckets =
                (List<ParsedDateHistogram.ParsedBucket>) parsedDateHistogram.getBuckets();
        for(ParsedDateHistogram.ParsedBucket histogrambucket: buckets) {

            String dateKeyAsString = histogrambucket.getKeyAsString();
            int passedCount = 0;
            int notPassedCount = 0;

            Aggregations histogramAggs = histogrambucket.getAggregations();
            ParsedLongTerms passTerm = histogramAggs.get("pass");
            List<ParsedLongTerms.ParsedBucket> passTermBuckets = (List<ParsedLongTerms.ParsedBucket>)passTerm.getBuckets();
            for(ParsedLongTerms.ParsedBucket passTermBucket : passTermBuckets) {
                long termKey = (long)passTermBucket.getKey();
                long count = passTermBucket.getDocCount();
                if(termKey == 0) {
                    notPassedCount += count;
                }else if(termKey == 1) {
                    passedCount += count;
                }
            }
            BucketInfo bucketInfo = BucketInfo.builder()
                    .notPassedCount(notPassedCount)
                    .passedCount(passedCount)
                    .key(dateKeyAsString)
                    .build();

            resutltBuckets.add(bucketInfo);
            totalCount += passedCount;
            totalCount += notPassedCount;
            totalPassedCount += passedCount;
            totalNotPassedCount += notPassedCount;
        }

        AggDateHistogramRsp aggDateHistogramRsp = AggDateHistogramRsp.builder()
                .total(totalCount)
                .totalNotPassed(totalNotPassedCount)
                .totalPassed(totalPassedCount)
                .buckets(resutltBuckets)
                .build();

        return aggDateHistogramRsp;

    }

    @Override
    public AggTermRsp createTerm(AggTermReq aggTermReq) {
        log.info("enter createTerm, AggTermReq= {}", aggTermReq);

        String targetTerm = aggTermReq.getTargetTerm();

        String aggName = "runs_over_term";

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders
                .terms(aggName)
                .field(targetTerm)
                .size(aggTermReq.getTermSize())
                .subAggregation(AggregationBuilders.terms("pass").field("passed"));

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp")
                .gte(aggTermReq.getStartTime())
                .lte(aggTermReq.getEndTime());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter().add(rangeQueryBuilder);

        if(StringUtils.hasText(aggTermReq.getRunTag())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("runTag.keyword", aggTermReq.getRunTag());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }

        if(StringUtils.hasText(aggTermReq.getProjectName())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("projectName", aggTermReq.getProjectName());
            boolQueryBuilder.filter().add(termQueryBuilder);
        }

        if (!targetTerm.equals("templateName")) {
            if (StringUtils.hasText(aggTermReq.getTemplateName())) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("templateName", aggTermReq.getTemplateName());
                boolQueryBuilder.filter().add(termQueryBuilder);
            }
        }

        if(!targetTerm.equals("environmentName")) {
            if(StringUtils.hasText(aggTermReq.getEnvironmentName()) && !aggTermReq.getEnvironmentName().equals("all")) {
                TermQueryBuilder termQueryBuilder =
                        QueryBuilders.termQuery("environmentName", aggTermReq.getEnvironmentName());
                boolQueryBuilder.filter().add(termQueryBuilder);
            }
        }

        NativeSearchQueryBuilder nativeQueryBuilder =  new NativeSearchQueryBuilder();
        nativeQueryBuilder.withPageable(PageRequest.of(0,1))
                .withAggregations(termsAggregationBuilder)
                .withQuery(boolQueryBuilder);

        SearchHits<JmsJobRunResultFull> searchHints =
                elasticsearchOperations.search(nativeQueryBuilder.build(), JmsJobRunResultFull.class);

        List<BucketInfo> resutltBuckets = new ArrayList<>();
        int totalCount = 0;
        int totalPassedCount = 0;
        int totalNotPassedCount = 0;
        AggregationsContainer<Aggregations> aggregationsContainer =
                (AggregationsContainer<Aggregations>) searchHints.getAggregations();
        Aggregations aggregations = aggregationsContainer.aggregations();
        ParsedStringTerms paredTerms = aggregations.get(aggName);
        List<ParsedStringTerms.ParsedBucket> buckets =
                (List<ParsedStringTerms.ParsedBucket>) paredTerms.getBuckets();
        for(ParsedStringTerms.ParsedBucket termsBucket: buckets) {

            String dateKeyAsString = termsBucket.getKeyAsString();
            int passedCount = 0;
            int notPassedCount = 0;

            Aggregations histogramAggs = termsBucket.getAggregations();
            ParsedLongTerms passTerm = histogramAggs.get("pass");
            List<ParsedLongTerms.ParsedBucket> passTermBuckets = (List<ParsedLongTerms.ParsedBucket>)passTerm.getBuckets();
            for(ParsedLongTerms.ParsedBucket passTermBucket : passTermBuckets) {
                long termKey = (long)passTermBucket.getKey();
                long count = passTermBucket.getDocCount();
                if(termKey == 0) {
                    notPassedCount += count;
                }else if(termKey == 1) {
                    passedCount += count;
                }
            }
            BucketInfo bucketInfo = BucketInfo.builder()
                    .notPassedCount(notPassedCount)
                    .passedCount(passedCount)
                    .key(dateKeyAsString)
                    .build();

            resutltBuckets.add(bucketInfo);
            totalCount += passedCount;
            totalCount += notPassedCount;
            totalPassedCount += passedCount;
            totalNotPassedCount += notPassedCount;
        }

        AggTermRsp aggTermRsp = AggTermRsp.builder()
                .total(totalCount)
                .totalNotPassed(totalNotPassedCount)
                .totalPassed(totalPassedCount)
                .buckets(resutltBuckets)
                .build();

        return aggTermRsp;

    }



}
