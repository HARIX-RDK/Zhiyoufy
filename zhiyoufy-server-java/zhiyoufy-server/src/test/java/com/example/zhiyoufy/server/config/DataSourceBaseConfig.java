package com.example.zhiyoufy.server.config;

import javax.sql.DataSource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@TestConfiguration
public class DataSourceBaseConfig {
	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().generateUniqueName(true)
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8").ignoreFailedDrops(true)
				.addScript("db/test/schema-ums.sql")
				.addScript("db/test/schema-ems.sql")
				.addScript("db/test/schema-pms.sql")
				.addScript("db/test/schema-wms.sql")
				.addScript("db/test/schema-scheduled-jobs.sql")
				.addScript("db/test/dataload-ums.sql")
				.addScript("db/test/dataload-ems.sql")
				.addScript("db/test/dataload-ems-configs.sql")
				.addScript("db/test/dataload-pms.sql")
				.addScript("db/test/dataload-wms.sql")
				.addScript("db/test/dataload-scheduled-jobs.sql")
				.build();
	}
}
