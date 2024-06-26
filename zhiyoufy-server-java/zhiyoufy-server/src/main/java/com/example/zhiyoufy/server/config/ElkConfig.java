package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.common.elk.IElkRecordFactory;
import com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElkConfig {
	@Bean
	public IElkRecordFactory elkRecordFactory() {
		return new ZhiyoufyElkRecordFactory();
	}
}
