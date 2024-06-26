package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import com.example.zhiyoufy.server.service.impl.WmsWorkerAppServiceImpl;
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
public class WmsWorkerAppServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	WmsWorkerAppService workerAppService;

	@Autowired
	WmsWorkerAppServiceImpl workerAppServiceImpl;
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
	public void add_workerApp_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String workerAppName = "new.workerApp";
		WmsWorkerAppParam workerAppParam = WmsWorkerAppParam.builder()
				.name(workerAppName)
				.build();

		// when
		WmsWorkerApp wmsWorkerApp = workerAppService.addWorkerApp(workerAppParam);

		// then
		assertThat(wmsWorkerApp).isNotNull();

		log.debug("wmsWorkerApp {}", StrUtils.jsonDump(wmsWorkerApp));

		assertThat(wmsWorkerApp.getName()).isEqualTo(workerAppName);
	}

	@Test
	@Order(200)
	public void del_workerApp_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		// when
		WmsWorkerApp wmsWorkerApp = workerAppService.getWorkerAppByName(workerAppName);

		// then
		assertThat(wmsWorkerApp).isNotNull();

		log.debug("wmsWorkerApp {}", StrUtils.jsonDump(wmsWorkerApp));

		assertThat(wmsWorkerApp.getName()).isEqualTo(workerAppName);

		// given
		Long workerAppId = wmsWorkerApp.getId();

		// when
		DeleteInfo deleteInfo = workerAppService.delWorkerAppById(workerAppId);

		log.debug("deleteInfo {}", StrUtils.jsonDump(deleteInfo));

		// then
		assertThat(deleteInfo.getDeleted()).isEqualTo(1);
	}

	@Test
	@Order(300)
	public void get_workerApp_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		CommonPage<WmsWorkerAppFull> workerAppPage = getWorkerAppList("W");

		// then
		assertThat(workerAppPage).isNotNull();

		assertThat(workerAppPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(310)
	public void get_workerApp_base_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		List<WmsWorkerAppBase> workerAppBaseList =
				workerAppService.getWorkerAppBaseList();

		// then
		assertThat(workerAppBaseList).isNotNull();

		log.debug("workerAppBaseList {}", StrUtils.jsonDump(workerAppBaseList));

		assertThat(workerAppBaseList.size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_workerApp_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();;

		// when
		WmsWorkerApp wmsWorkerApp = workerAppService.getWorkerAppByName(workerAppName);

		// then
		assertThat(wmsWorkerApp).isNotNull();

		log.debug("wmsWorkerApp {}", StrUtils.jsonDump(wmsWorkerApp));

		assertThat(wmsWorkerApp.getName()).isEqualTo(workerAppName);

		// given
		Long workerAppId = wmsWorkerApp.getId();
		String newName = workerAppName + "_updated";
		WmsWorkerAppUpdateParam updateParam = new WmsWorkerAppUpdateParam();
		updateParam.setName(newName);

		// when
		UpdateInfo updateInfo = workerAppService.updateWorkerApp(workerAppId, updateParam);

		// then
		assertThat(updateInfo.getUpdated()).isEqualTo(1);

		// when
		wmsWorkerApp = workerAppService.getWorkerAppById(workerAppId);

		// then
		assertThat(wmsWorkerApp).isNotNull();

		log.debug("wmsWorkerApp {}", StrUtils.jsonDump(wmsWorkerApp));

		assertThat(wmsWorkerApp.getName()).isEqualTo(newName);
	}

	@Test
	@Order(1000)
	public void add_workerApp_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSwe001 = serviceTestHelper.getUserByName("swe001");

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		WmsWorkerAppUserRelationFull relationFull = new WmsWorkerAppUserRelationFull();
		relationFull.setWorkerAppId(workerApp.getId());
		relationFull.setUserId(userSwe001.getId());
		relationFull.setUsername(userSwe001.getUsername());
		relationFull.setIsOwner(false);
		relationFull.setIsEditor(true);

		// when
		int cnt = workerAppService.addWorkerAppUserRelation(relationFull);

		// then
		assertThat(cnt).isEqualTo(1);

		// when
		CommonPage<WmsWorkerAppUserRelationFull> relationPage =
				workerAppService.getWorkerAppUserRelationListByWorkerAppId(
						workerApp.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(1010)
	public void del_workerApp_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSet001 = serviceTestHelper.getUserByName("set001");

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		// when
		CommonPage<WmsWorkerAppUserRelationFull> relationPage =
				workerAppService.getWorkerAppUserRelationListByWorkerAppId(
						workerApp.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);

		WmsWorkerAppUserRelationFull relationFull = relationPage.getList().stream()
				.filter(relation -> {
					return relation.getUsername().equals(userSet001.getUsername());
				})
				.findAny()
				.orElse(null);

		assertThat(relationFull).isNotNull();

		// when
		int cnt = workerAppService.delWorkerAppUserRelationById(relationFull.getId());

		// then
		assertThat(cnt).isEqualTo(1);
	}

	@Test
	@Order(1020)
	public void get_workerApp_user_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		// when
		CommonPage<WmsWorkerAppUserRelationFull> relationPage =
				workerAppService.getWorkerAppUserRelationListByWorkerAppId(
						workerApp.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(0);
	}

	private CommonPage<WmsWorkerAppFull> getWorkerAppList(String keyword) {
		// given
		WmsWorkerAppQueryParam queryParam = new WmsWorkerAppQueryParam();
		queryParam.setKeyword(keyword);
		queryParam.setSortBy("name");

		// when
		CommonPage<WmsWorkerAppFull> workerAppPage = workerAppService.getWorkerAppList(
				queryParam, 20, 1);

		// then
		assertThat(workerAppPage).isNotNull();

		log.debug("workerAppPage {}", StrUtils.jsonDump(workerAppPage));

		return workerAppPage;
	}
}
