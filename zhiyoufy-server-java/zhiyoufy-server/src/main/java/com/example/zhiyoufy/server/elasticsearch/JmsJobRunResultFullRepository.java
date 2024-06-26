package com.example.zhiyoufy.server.elasticsearch;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JmsJobRunResultFullRepository
		extends ElasticsearchRepository<JmsJobRunResultFull, String> {
}
