package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupUpdateParam;
import com.example.zhiyoufy.server.service.impl.WmsWorkerGroupServiceImpl;
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
public class WmsWorkerGroupServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	WmsWorkerAppService workerAppService;

//	@Autowired
//	WmsGroupTokenService groupTokenService;

	@Autowired
	WmsWorkerGroupService workerGroupService;

	@Autowired
	WmsWorkerGroupServiceImpl workerGroupServiceImpl;
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
	public void add_worker_group_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		// given
		String workerGroupName = "new.workerGroup";
		WmsWorkerGroupParam workerGroupParam = WmsWorkerGroupParam.builder()
				.workerAppId(workerApp.getId())
				.workerAppName(workerApp.getName())
				.name(workerGroupName)
				.build();

		// when
		WmsWorkerGroup workerGroup =
				workerGroupService.addWorkerGroup(workerGroupParam);

		// then
		assertThat(workerGroup).isNotNull();

		log.debug("workerGroup {}", StrUtils.jsonDump(workerGroup));

		assertThat(workerGroup.getName()).isEqualTo(workerGroupName);
	}

	@Test
	@Order(200)
	public void del_worker_group_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		Long workerAppId = workerApp.getId();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		// when
		WmsWorkerGroup workerGroup =
				workerGroupService.getWorkerGroupByWorkerAppIdAndName(workerAppId, workerGroupName);

		// then
		assertThat(workerGroup).isNotNull();

		log.debug("workerGroup {}", StrUtils.jsonDump(workerGroup));

		assertThat(workerGroup.getName()).isEqualTo(workerGroupName);

		// given
		Long workerGroupId = workerGroup.getId();

//		WmsGroupTokenQueryParam queryParam = new WmsGroupTokenQueryParam();
//
//		// when
//		CommonPage<WmsGroupToken> groupTokenPage =
//				groupTokenService.getConfigItemList(
//						workerGroupId, queryParam, 20, 1);
//
//		log.debug("groupTokenPage before delete {}", StrUtils.jsonDump(groupTokenPage));
//
//		// then
//		assertThat(groupTokenPage).isNotNull();
//		assertThat(groupTokenPage.getTotal()).isGreaterThan(0);

		// when
		DeleteInfo deleteInfo = workerGroupService.delWorkerGroupById(workerGroupId);

//		Throwable thrown = catchThrowable(
//				() -> groupTokenService.getConfigItemList(
//						workerGroupId, queryParam, 20, 1));

		// then
		assertThat(deleteInfo.getDeleted()).isEqualTo(1);

//		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(300)
	public void get_worker_group_list_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		Long workerAppId = workerApp.getId();

		WmsWorkerGroupQueryParam queryParam = new WmsWorkerGroupQueryParam();
		queryParam.setKeyword("WorkerGroup_001");

		// when
		CommonPage<WmsWorkerGroup> workerGroupPage =
				workerGroupService.getWorkerGroupList(workerAppId, queryParam, 20, 1);

		// then
		assertThat(workerGroupPage).isNotNull();

		assertThat(workerGroupPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_worker_group_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		Long workerAppId = workerApp.getId();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();
		String workerGroupUpdatedName = workerGroupName + "_updated";

		WmsWorkerGroup workerGroup =
				workerGroupService.getWorkerGroupByWorkerAppIdAndName(workerAppId, workerGroupName);

		WmsWorkerGroupUpdateParam updateParam = WmsWorkerGroupUpdateParam.builder()
				.name(workerGroupUpdatedName)
				.build();

		// when
		UpdateInfo updateInfo = workerGroupService.updateWorkerGroup(workerGroup.getId(), updateParam);

		// then
		assertThat(updateInfo.getUpdated()).isEqualTo(1);

		// when
		WmsWorkerGroup workerGroupUpdated =
				workerGroupService.getWorkerGroupById(workerGroup.getId());

		// then
		assertThat(workerGroupUpdated).isNotNull();

		log.debug("workerGroupUpdated {}", StrUtils.jsonDump(workerGroupUpdated));

		assertThat(workerGroupUpdated.getName()).isEqualTo(workerGroupUpdatedName);
	}
}
