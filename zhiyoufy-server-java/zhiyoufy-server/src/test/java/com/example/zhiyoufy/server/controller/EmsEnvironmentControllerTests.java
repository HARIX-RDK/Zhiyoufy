package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.config.ControllerTestGroupConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ums.FormLoginParam;
import com.example.zhiyoufy.server.domain.dto.ums.LoginResponseData;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import com.example.zhiyoufy.server.support.controller.ControllerTestHelper;
import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;
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
public class EmsEnvironmentControllerTests {
	@Autowired
	ControllerTestHelper controllerTestHelper;

	@Autowired
	ZhiyoufyTestProperties zhiyoufyTestProperties;

	@LocalServerPort
	int port = 0;

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
	public void login_admin_then_add_environment_should_be_ok() {
		LoginResponseData loginResponseData = controllerTestHelper.formLoginAdmin();

		// given
		String newEnvName = "new.env";
		EmsEnvironmentParam environmentParam = EmsEnvironmentParam.builder()
				.name(newEnvName)
				.build();

		// when
		CommonResult<EmsEnvironment> result = webClient.post()
				.uri("/zhiyoufy-api/v1/environment/add-environment")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.bodyValue(environmentParam)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<EmsEnvironment>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		// given
		EmsEnvironment emsEnvironment = result.getData();

		// then
		assertThat(emsEnvironment.getName()).isEqualTo(newEnvName);
	}

	@Test
	@Order(200)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void login_admin_then_get_environment_list_should_be_ok() {
		LoginResponseData loginResponseData = controllerTestHelper.formLoginAdmin();

		// when
		CommonResult<CommonPage<EmsEnvironmentFull>> result = webClient.get()
				.uri("/zhiyoufy-api/v1/environment/environment-list")
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<CommonPage<EmsEnvironmentFull>>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		log.debug("result {}", StrUtils.jsonDump(result));

		// given
		List<EmsEnvironmentFull> environmentList = result.getData().getList();

		// then
		assertThat(environmentList.size()).isGreaterThan(0);
	}

	@Test
	@Order(300)
	@DirtiesContext
	@EnabledIf(expression = "${zhiyoufy-test.controller-test.dirties-context-on}", loadContext = true)
	public void del_environment_should_be_ok() {
		LoginResponseData loginResponseData = controllerTestHelper.formLoginAdmin();
		String envName = "env_001";

		// when
		EmsEnvironmentFull environmentFull =
				controllerTestHelper.getEnvironmentByName(envName);

		// then
		assertThat(environmentFull).isNotNull();

		// when
		CommonResult<Integer> delResult = webClient.delete()
				.uri("/zhiyoufy-api/v1/environment/del-environment/" + environmentFull.getId())
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<Integer>>() {})
				.block();

		// then
		assertThat(delResult).isNotNull();

		log.debug("delResult {}", StrUtils.jsonDump(delResult));

		Integer delCnt = delResult.getData();

		assertThat(delCnt).isEqualTo(1);
	}
}
