package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemUpdateParam;
import com.example.zhiyoufy.server.service.impl.EmsConfigItemServiceImpl;
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
public class EmsConfigItemServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	EmsEnvironmentService environmentService;

	@Autowired
	EmsConfigCollectionService configCollectionService;

	@Autowired
	EmsConfigItemService configItemService;

	@Autowired
	EmsConfigItemServiceImpl configItemServiceImpl;

	String defaultEnvName;
	//endregion

	//region setup & cleanup
	@BeforeEach
	public void setup(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		defaultEnvName = serviceTestHelper.getDefaultEnvName();
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
	public void add_config_item_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = serviceTestHelper.getDefaultEnvName();
		String collectionName = serviceTestHelper.getDefaultCollectionName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);
		EmsConfigCollection configCollection = serviceTestHelper.getConfigCollectionByEnvAndName(
				environment, collectionName);

		// given
		String configItemName = "new.configItem";
		EmsConfigItemParam configItemParam = EmsConfigItemParam.builder()
				.environmentId(environment.getId())
				.environmentName(environment.getName())
				.collectionId(configCollection.getId())
				.collectionName(configCollection.getName())
				.name(configItemName)
				.configValue("# add_config_item_should_be_ok")
				.build();

		// when
		EmsConfigItem configItem =
				configItemService.addConfigItem(configItemParam);

		// then
		assertThat(configItem).isNotNull();

		log.debug("configItem {}", StrUtils.jsonDump(configItem));

		assertThat(configItem.getName()).isEqualTo(configItemName);
	}

	@Test
	@Order(200)
	public void del_config_item_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = serviceTestHelper.getDefaultEnvName();
		String collectionName = serviceTestHelper.getDefaultCollectionName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);
		EmsConfigCollection configCollection = serviceTestHelper.getConfigCollectionByEnvAndName(
				environment, collectionName);

		Long collectionId = configCollection.getId();
		String configItemName = serviceTestHelper.getDefaultItemName();

		// when
		EmsConfigItem configItem =
				configItemService.getConfigItemByCollectionIdAndName(
						collectionId, configItemName);

		// then
		assertThat(configItem).isNotNull();

		log.debug("configItem {}", StrUtils.jsonDump(configItem));

		assertThat(configItem.getName()).isEqualTo(configItemName);

		// given
		Long configItemId = configItem.getId();

		// when
		int deleted = configItemService.delConfigItemById(configItemId);

		// then
		assertThat(deleted).isEqualTo(1);
	}

	@Test
	@Order(300)
	public void get_config_item_list_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = serviceTestHelper.getDefaultEnvName();
		String collectionName = serviceTestHelper.getDefaultCollectionName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);
		EmsConfigCollection configCollection = serviceTestHelper.getConfigCollectionByEnvAndName(
				environment, collectionName);

		Long collectionId = configCollection.getId();

		EmsConfigItemQueryParam queryParam = new EmsConfigItemQueryParam();
		queryParam.setKeyword("ConfigItem_001");

		// when
		CommonPage<EmsConfigItem> configItemPage =
				configItemService.getConfigItemList(collectionId, queryParam, 20, 1);

		// then
		assertThat(configItemPage).isNotNull();

		log.debug("configItemPage {}", StrUtils.jsonDump(configItemPage));

		assertThat(configItemPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(310)
	public void get_run_id_list_should_be_ok() {
		// when
		List<String> runIdList = configItemService.getRunIdListByInUse();

		// then
		assertThat(runIdList).isNotNull();

		log.debug("runIdList {}", StrUtils.jsonDump(runIdList));

		assertThat(runIdList.size()).isEqualTo(2);
	}

	@Test
	@Order(400)
	public void update_config_item_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String envName = serviceTestHelper.getDefaultEnvName();
		String collectionName = serviceTestHelper.getDefaultCollectionName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);
		EmsConfigCollection configCollection = serviceTestHelper.getConfigCollectionByEnvAndName(
				environment, collectionName);

		Long collectionId = configCollection.getId();
		String configItemName = serviceTestHelper.getDefaultItemName();

		// when
		EmsConfigItem configItem =
				configItemService.getConfigItemByCollectionIdAndName(
						collectionId, configItemName);

		// then
		assertThat(configItem).isNotNull();

		// given
		String configItemUpdatedValue = "# update_config_item_should_be_ok";

		EmsConfigItemUpdateParam updateParam = EmsConfigItemUpdateParam.builder()
				.configValue(configItemUpdatedValue)
				.disabled(true)
				.build();

		// when
		int updated = configItemService.updateConfigItem(configItem.getId(), updateParam);

		// then
		assertThat(updated).isEqualTo(1);

		// when
		EmsConfigItem configItemUpdated =
				configItemService.getConfigItemById(configItem.getId());

		// then
		assertThat(configItemUpdated).isNotNull();

		log.debug("configItemUpdated {}", StrUtils.jsonDump(configItemUpdated));

		assertThat(configItemUpdated.getConfigValue()).isEqualTo(configItemUpdatedValue);
		assertThat(configItemUpdated.getDisabled()).isTrue();
	}
}
