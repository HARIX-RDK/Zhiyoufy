package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParam;
import com.example.zhiyoufy.server.service.impl.EmsConfigCollectionServiceImpl;
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
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ServiceTestGroupGeneralConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Transactional
@Slf4j
public class EmsConfigCollectionServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	EmsEnvironmentService environmentService;

	@Autowired
	EmsConfigItemService configItemService;

	@Autowired
	EmsConfigCollectionService configCollectionService;

	@Autowired
	EmsConfigCollectionServiceImpl configCollectionServiceImpl;

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
	public void add_config_collection_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		// given
		String configCollectionName = "new.configCollection";
		EmsConfigCollectionParam configCollectionParam = EmsConfigCollectionParam.builder()
				.environmentId(environment.getId())
				.environmentName(environment.getName())
				.name(configCollectionName)
				.build();

		// when
		EmsConfigCollection configCollection =
				configCollectionService.addConfigCollection(configCollectionParam);

		// then
		assertThat(configCollection).isNotNull();

		log.debug("configCollection {}", StrUtils.jsonDump(configCollection));

		assertThat(configCollection.getName()).isEqualTo(configCollectionName);
	}

	@Test
	@Order(200)
	public void del_config_collection_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = serviceTestHelper.getDefaultEnvName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();
		String configCollectionName = serviceTestHelper.getDefaultCollectionName();

		// when
		EmsConfigCollection configCollection =
				configCollectionService.getConfigCollectionByEnvIdAndName(envId, configCollectionName);

		// then
		assertThat(configCollection).isNotNull();

		log.debug("configCollection {}", StrUtils.jsonDump(configCollection));

		assertThat(configCollection.getName()).isEqualTo(configCollectionName);

		// given
		Long configCollectionId = configCollection.getId();

		EmsConfigItemQueryParam queryParam = new EmsConfigItemQueryParam();

		// when
		CommonPage<EmsConfigItem> configItemPage =
				configItemService.getConfigItemList(
						configCollectionId, queryParam, 20, 1);

		log.debug("configItemPage before delete {}", StrUtils.jsonDump(configItemPage));

		// then
		assertThat(configItemPage).isNotNull();
		assertThat(configItemPage.getTotal()).isGreaterThan(0);

		// when
		int deleted = configCollectionService.delConfigCollectionById(configCollectionId);

		Throwable thrown = catchThrowable(
				() -> configItemService.getConfigItemList(
						configCollectionId, queryParam, 20, 1));

		// then
		assertThat(deleted).isEqualTo(1);

		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(300)
	public void get_config_collection_list_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();

		EmsConfigCollectionQueryParam queryParam = new EmsConfigCollectionQueryParam();
		queryParam.setKeyword("ConfigCollection_001");

		// when
		CommonPage<EmsConfigCollection> configCollectionPage =
				configCollectionService.getConfigCollectionList(envId, queryParam, 20, 1);

		// then
		assertThat(configCollectionPage).isNotNull();

		assertThat(configCollectionPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_config_collection_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = defaultEnvName;

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		Long envId = environment.getId();
		String configCollectionName = "ConfigCollection_001";
		String configCollectionUpdatedName = "ConfigCollection_001_updated";

		EmsConfigCollection configCollection =
				configCollectionService.getConfigCollectionByEnvIdAndName(envId, configCollectionName);

		EmsConfigCollectionUpdateParam updateParam = EmsConfigCollectionUpdateParam.builder()
				.name(configCollectionUpdatedName)
				.build();

		// when
		int updated = configCollectionService.updateConfigCollection(configCollection.getId(), updateParam);

		// then
		assertThat(updated).isEqualTo(1);

		// when
		EmsConfigCollection configCollectionUpdated =
				configCollectionService.getConfigCollectionById(configCollection.getId());

		// then
		assertThat(configCollectionUpdated).isNotNull();

		log.debug("configCollectionUpdated {}", StrUtils.jsonDump(configCollectionUpdated));

		assertThat(configCollectionUpdated.getName()).isEqualTo(configCollectionUpdatedName);
	}
}
