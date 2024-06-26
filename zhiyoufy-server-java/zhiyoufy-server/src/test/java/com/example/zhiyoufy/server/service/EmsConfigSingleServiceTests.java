package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleUpdateParam;
import com.example.zhiyoufy.server.service.impl.EmsConfigSingleServiceImpl;
import com.example.zhiyoufy.server.support.service.ServiceTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ServiceTestGroupGeneralConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Transactional
@Slf4j
public class EmsConfigSingleServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	EmsEnvironmentService environmentService;

	@Autowired
	EmsConfigSingleService configSingleService;

	@Autowired
	EmsConfigSingleServiceImpl configSingleServiceImpl;

	String defaultEnvName = "env_for_configs";
	//endregion

	//region setup & cleanup
	@BeforeEach
	public void setup(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);
	}

	@AfterEach
	public void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);

		serviceTestHelper.clearAuthentication();
	}
	//endregion

	@Test
	@Order(100)
	public void add_config_single_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		// given
		String configSingleName = "new.configSingle";
		EmsConfigSingleParam configSingleParam = EmsConfigSingleParam.builder()
				.environmentId(environment.getId())
				.environmentName(environment.getName())
				.name(configSingleName)
				.build();

		// when
		EmsConfigSingle configSingle =
				configSingleService.addConfigSingle(configSingleParam);

		// then
		assertThat(configSingle).isNotNull();

		log.debug("configSingle {}", StrUtils.jsonDump(configSingle));

		assertThat(configSingle.getName()).isEqualTo(configSingleName);
	}

	@Test
	@Order(200)
	public void del_config_single_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();
		String configSingleName = "ConfigSingle_001";

		// when
		EmsConfigSingle configSingle =
				configSingleService.getConfigSingleByEnvIdAndName(envId, configSingleName);

		// then
		assertThat(configSingle).isNotNull();

		log.debug("configSingle {}", StrUtils.jsonDump(configSingle));

		assertThat(configSingle.getName()).isEqualTo(configSingleName);

		// given
		Long configSingleId = configSingle.getId();

		// when
		int deleted = configSingleService.delConfigSingleById(configSingleId);

		// then
		assertThat(deleted).isEqualTo(1);
	}

	@Test
	@Order(300)
	public void get_config_single_list_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();

		EmsConfigSingleQueryParam queryParam = new EmsConfigSingleQueryParam();
		queryParam.setKeyword("ConfigSingle_001");

		// when
		CommonPage<EmsConfigSingle> configSinglePage =
				configSingleService.getConfigSingleList(envId, queryParam, 20, 1);

		// then
		assertThat(configSinglePage).isNotNull();

		assertThat(configSinglePage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_config_single_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();
		String configSingleName = "ConfigSingle_001";
		String configSingleUpdatedValue = "# update_config_single_should_be_ok";

		EmsConfigSingle configSingle =
				configSingleService.getConfigSingleByEnvIdAndName(envId, configSingleName);

		EmsConfigSingleUpdateParam updateParam = EmsConfigSingleUpdateParam.builder()
				.configValue(configSingleUpdatedValue)
				.build();

		// when
		int updated = configSingleService.updateConfigSingle(configSingle.getId(), updateParam);

		// then
		assertThat(updated).isEqualTo(1);

		// when
		EmsConfigSingle configSingleUpdated =
				configSingleService.getConfigSingleById(configSingle.getId());

		// then
		assertThat(configSingleUpdated).isNotNull();

		log.debug("configSingleUpdated {}", StrUtils.jsonDump(configSingleUpdated));

		assertThat(configSingleUpdated.getConfigValue()).isEqualTo(configSingleUpdatedValue);
	}
}
