package com.example.zhiyoufy.server.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.example.zhiyoufy.server.service.EmailService;
import com.example.zhiyoufy.server.service.impl.EmailServiceImpl;
import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

@Configuration
@EnableConfigurationProperties({ZhiyoufyServerProperties.class})
public class AppConfig {
	@ConditionalOnMissingBean
	@Bean
	EmailService emailService() {
		return new EmailServiceImpl();
	}

	@Bean
	EventBus eventBus() {
		return new EventBus();
	}

	@Bean
	@Qualifier("zhiyoufy-executor")
	ExecutorService zhiyoufyExecutorService(ZhiyoufyServerProperties properties) {
		ExecutorService executorService =
				Executors.newFixedThreadPool(
						properties.getThreadPoolSize(),
						new CustomizableThreadFactory("zhiyoufy-executor-"));
		return executorService;
	}

	@Bean
	@Qualifier("zhiyoufy-timer")
	ScheduledExecutorService zhiyoufyScheduledExecutorService(ZhiyoufyServerProperties properties) {
		ScheduledExecutorService scheduledExecutorService =
				Executors.newScheduledThreadPool(
						properties.getTimerThreadPoolSize(),
						new CustomizableThreadFactory("zhiyoufy-timer-"));
		return scheduledExecutorService;
	}
}
