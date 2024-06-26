package com.example.zhiyoufy.server.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({
		DataSourceBaseConfig.class,
		ServiceTestConfig.class,
		BaseTestConfig.class})
public class ServiceTestGroupGeneralConfig {
}
