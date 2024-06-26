package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.server.config.ControllerTestGroupConfig;
import com.example.zhiyoufy.server.domain.dto.ums.FormLoginParam;
import com.example.zhiyoufy.server.domain.dto.ums.LoginResponseData;
import com.example.zhiyoufy.server.domain.dto.ums.RequestIdentificationCodeParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import com.example.zhiyoufy.server.domain.dto.ums.UserInfoData;
import com.example.zhiyoufy.server.support.controller.ControllerTestHelper;
import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;
import com.example.zhiyoufy.server.support.service.FakeEmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ControllerTestGroupConfig.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UmsUserControllerTests {
	@Autowired
	FakeEmailServiceImpl fakeEmailService;
	@Autowired
	ControllerTestHelper controllerTestHelper;

	@LocalServerPort
	int port = 0;

	@Autowired
	ZhiyoufyTestProperties zhiyoufyTestProperties;

	WebClient webClient;

	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		Hooks.onOperatorDebug();

		controllerTestHelper.setPort(port);
		webClient = controllerTestHelper.getWebClient("");
	}

	@AfterEach
	public void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);
	}

	@Test
	@Order(100)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void requestIdentificationCode_should_be_ok() {
		// given
		RequestIdentificationCodeParam codeParam = RequestIdentificationCodeParam.builder()
				.kind("email")
				.email("new.user@example.com")
				.build();
		fakeEmailService.reset();

		// when
		// new ParameterizedTypeReference<CommonResult<?>>() {}
		CommonResult result = webClient.post()
				.uri("/zhiyoufy-api/v1/user/request-id-code")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(codeParam)
				.retrieve()
				.bodyToMono(CommonResult.class).block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		assertThat(fakeEmailService.getSendCnt()).isEqualTo(1);

		log.debug("mail text: {}", fakeEmailService.getLastText());
	}

	@Test
	@Order(200)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void form_login_then_get_user_info_should_be_ok() {
		// given
		FormLoginParam loginParam = FormLoginParam.builder()
				.username("sysadmin")
				.password(zhiyoufyTestProperties.getAdminPassword())
				.build();

		// when
		CommonResult<LoginResponseData> result = webClient.post()
				.uri("/zhiyoufy-api/v1/user/form-login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(loginParam)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<LoginResponseData>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		// given
		LoginResponseData loginResponseData = result.getData();

		// when
		CommonResult<UserInfoData> userInfoResult = webClient.get()
				.uri("/zhiyoufy-api/v1/user/user-info")
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<UserInfoData>>() {})
				.block();

		// then
		assertThat(userInfoResult).isNotNull();

		UserInfoData userInfoData = userInfoResult.getData();

		assertThat(userInfoData).isNotNull();
		assertThat(userInfoData.getUsername()).isEqualTo("sysadmin");
		assertThat(userInfoData.getRoles().contains("sysAdmin")).isTrue();
	}

	@Test
	@Order(300)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void login_admin_then_get_user_list_should_be_ok() {
		// given
		FormLoginParam loginParam = FormLoginParam.builder()
				.username("admin")
				.password(zhiyoufyTestProperties.getAdminPassword())
				.build();

		// when
		CommonResult<LoginResponseData> result = webClient.post()
				.uri("/zhiyoufy-api/v1/user/form-login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(loginParam)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<LoginResponseData>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		// given
		LoginResponseData loginResponseData = result.getData();

		// when
		CommonResult<CommonPage<UmsUserDTO>> userListResult = webClient.get()
				.uri("/zhiyoufy-api/v1/user/user-list")
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<CommonPage<UmsUserDTO>>>() {})
				.block();

		// then
		assertThat(userListResult).isNotNull();

		log.debug("userListResult {}", StrUtils.jsonDump(userListResult));

		assertThat(userListResult.getError()).isNull();

		CommonPage<UmsUserDTO> userPage = userListResult.getData();

		assertThat(userPage).isNotNull();
		assertThat(userPage.getList()).isNotNull();
		assertThat(userPage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(300)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void login_set001_then_get_user_list_should_fail() {
		// given
		FormLoginParam loginParam = FormLoginParam.builder()
				.username("set001")
				.password(zhiyoufyTestProperties.getAdminPassword())
				.build();

		// when
		CommonResult<LoginResponseData> result = webClient.post()
				.uri("/zhiyoufy-api/v1/user/form-login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(loginParam)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<LoginResponseData>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		// given
		LoginResponseData loginResponseData = result.getData();

		// when
		CommonResult<CommonPage<UmsUserDTO>> userListResult = webClient.get()
				.uri("/zhiyoufy-api/v1/user/user-list")
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<CommonPage<UmsUserDTO>>>() {})
				.block();

		// then
		assertThat(userListResult).isNotNull();

		log.debug("userListResult {}", StrUtils.jsonDump(userListResult));

		assertThat(userListResult.getError()).isNotNull();
	}
}
