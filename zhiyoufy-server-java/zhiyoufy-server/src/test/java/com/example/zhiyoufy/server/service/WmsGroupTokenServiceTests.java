package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenUpdateParam;
import com.example.zhiyoufy.server.service.impl.WmsGroupTokenServiceImpl;
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
public class WmsGroupTokenServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	WmsWorkerAppService workerAppService;

	@Autowired
	WmsWorkerGroupService workerGroupService;

	@Autowired
	WmsGroupTokenService groupTokenService;

	@Autowired
	WmsGroupTokenServiceImpl groupTokenServiceImpl;
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
	public void add_group_token_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);
		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		// given
		String groupTokenName = "new.groupToken";
		WmsGroupTokenParam groupTokenParam = WmsGroupTokenParam.builder()
				.workerAppId(workerApp.getId())
				.workerAppName(workerApp.getName())
				.workerGroupId(workerGroup.getId())
				.workerGroupName(workerGroup.getName())
				.name(groupTokenName)
				.secret("# add_group_token_should_be_ok")
				.build();

		// when
		WmsGroupToken groupToken =
				groupTokenService.addGroupToken(groupTokenParam);

		// then
		assertThat(groupToken).isNotNull();

		log.debug("groupToken {}", StrUtils.jsonDump(groupToken));

		assertThat(groupToken.getName()).isEqualTo(groupTokenName);
	}

	@Test
	@Order(200)
	public void del_group_token_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);
		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		Long workerGroupId = workerGroup.getId();
		String groupTokenName = serviceTestHelper.getDefaultGroupTokenName();

		// when
		WmsGroupToken groupToken =
				groupTokenService.getGroupTokenByWorkerGroupIdAndName(
						workerGroupId, groupTokenName);

		// then
		assertThat(groupToken).isNotNull();

		log.debug("groupToken {}", StrUtils.jsonDump(groupToken));

		assertThat(groupToken.getName()).isEqualTo(groupTokenName);

		// given
		Long groupTokenId = groupToken.getId();

		// when
		DeleteInfo deleteInfo = groupTokenService.delGroupTokenById(groupTokenId);

		// then
		assertThat(deleteInfo.getDeleted()).isEqualTo(1);
	}

	@Test
	@Order(300)
	public void get_group_token_list_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);
		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		Long workerGroupId = workerGroup.getId();

		WmsGroupTokenQueryParam queryParam = new WmsGroupTokenQueryParam();
		queryParam.setKeyword("GroupToken_001");

		// when
		CommonPage<WmsGroupToken> groupTokenPage =
				groupTokenService.getGroupTokenList(workerGroupId, queryParam, 20, 1);

		// then
		assertThat(groupTokenPage).isNotNull();

		log.debug("groupTokenPage {}", StrUtils.jsonDump(groupTokenPage));

		assertThat(groupTokenPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_group_token_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();
		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);
		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		Long workerGroupId = workerGroup.getId();
		String groupTokenName = serviceTestHelper.getDefaultGroupTokenName();

		// when
		WmsGroupToken groupToken =
				groupTokenService.getGroupTokenByWorkerGroupIdAndName(
						workerGroupId, groupTokenName);

		// then
		assertThat(groupToken).isNotNull();

		// given
		String groupTokenUpdatedSecret = "# update_group_token_should_be_ok";

		WmsGroupTokenUpdateParam updateParam = WmsGroupTokenUpdateParam.builder()
				.secret(groupTokenUpdatedSecret)
				.build();

		// when
		UpdateInfo updateInfo = groupTokenService.updateGroupToken(
				groupToken.getId(), updateParam);

		// then
		assertThat(updateInfo.getUpdated()).isEqualTo(1);

		// when
		WmsGroupToken groupTokenUpdated =
				groupTokenService.getGroupTokenById(groupToken.getId());

		// then
		assertThat(groupTokenUpdated).isNotNull();

		log.debug("groupTokenUpdated {}", StrUtils.jsonDump(groupTokenUpdated));

		assertThat(groupTokenUpdated.getSecret()).isEqualTo(groupTokenUpdatedSecret);
	}
}
