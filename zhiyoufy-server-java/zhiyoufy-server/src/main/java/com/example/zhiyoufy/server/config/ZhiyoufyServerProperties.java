package com.example.zhiyoufy.server.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zhiyoufy.server")
@Getter
@Setter
public class ZhiyoufyServerProperties {
	private long tokenExpireAfterSecs = 24 * 3600;
	private int threadPoolSize = 10;
	private int timerThreadPoolSize = 2;
	private int activeWorkerSessionTimeout = 180;
	private long defaultReplyTimeout = 20;
	private String elasticsearchHostAndPort = "localhost:9200";
	private String elasticsearchUsername;
	private String elasticsearchPassword;
	private String timeZone = "UTC";
	private int finishedJobRunResultKeepSize = 100;
}
