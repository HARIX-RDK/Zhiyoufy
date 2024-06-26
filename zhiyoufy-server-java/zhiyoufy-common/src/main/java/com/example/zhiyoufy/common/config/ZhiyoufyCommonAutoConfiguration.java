package com.example.zhiyoufy.common.config;

import com.example.zhiyoufy.common.elk.DefaultElkSwitchManager;
import com.example.zhiyoufy.common.elk.IElkSwitchManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ZhiyoufyCommonProperties.class)
@ComponentScan("com.example.zhiyoufy.common")
public class ZhiyoufyCommonAutoConfiguration {
	@Autowired
	private ZhiyoufyCommonProperties zhiyoufyCommonProperties;

	@Bean
	@ConditionalOnMissingBean
	public IElkSwitchManager elkSwitchManager() {
		return new DefaultElkSwitchManager(zhiyoufyCommonProperties);
	}
}
