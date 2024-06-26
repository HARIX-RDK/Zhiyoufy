package com.example.zhiyoufy.server.config;

import org.elasticsearch.client.RestHighLevelClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
		basePackages = "com.example.zhiyoufy.server.elasticsearch"
)
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
	@Autowired
	ZhiyoufyServerProperties zhiyoufyServerProperties;

	@Override
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo(zhiyoufyServerProperties.getElasticsearchHostAndPort())
				.withBasicAuth(zhiyoufyServerProperties.getElasticsearchUsername(),
						zhiyoufyServerProperties.getElasticsearchPassword())
				.build();

		return RestClients.create(clientConfiguration).rest();
	}
}
