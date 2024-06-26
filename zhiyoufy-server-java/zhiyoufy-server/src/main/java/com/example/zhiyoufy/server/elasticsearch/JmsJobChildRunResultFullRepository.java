package com.example.zhiyoufy.server.elasticsearch;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JmsJobChildRunResultFullRepository
		extends ElasticsearchRepository<JmsJobChildRunResultFull, String> {
}
