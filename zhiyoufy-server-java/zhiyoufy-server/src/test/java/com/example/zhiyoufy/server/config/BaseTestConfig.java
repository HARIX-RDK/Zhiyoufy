package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BaseTestConfig {
	@Bean
	ZhiyoufyTestProperties zhiyoufyTestProperties() {
		return new ZhiyoufyTestProperties();
	}
}
