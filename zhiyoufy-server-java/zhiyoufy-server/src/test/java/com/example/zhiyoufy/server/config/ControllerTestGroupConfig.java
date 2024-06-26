package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.server.support.controller.ControllerTestHelper;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({DataSourceBaseConfig.class, MailTestConfig.class, BaseTestConfig.class})
public class ControllerTestGroupConfig {
	@Bean
	ControllerTestHelper controllerTestHelper() {
		return new ControllerTestHelper();
	}
}
