package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.server.service.EmailService;
import com.example.zhiyoufy.server.support.service.FakeEmailServiceImpl;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MailTestConfig {
	@Bean
	EmailService emailService() {
		return new FakeEmailServiceImpl();
	}
}
