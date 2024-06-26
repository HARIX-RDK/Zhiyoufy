package com.example.zhiyoufy.common.util;

import com.example.zhiyoufy.common.config.ServiceTestConfig;
import com.example.zhiyoufy.common.support.FakeUser;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ServiceTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Slf4j
public class StrUtilsTests {
	FakeUser testUser;

	@BeforeEach
	void setUp(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		testUser = FakeUser.builder()
			.username("jsonDumpPrivateView")
			.password("jsonDumpPrivateView-password")
			.build();
	}

	@AfterEach
	void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);
	}

	@Test
	@Order(10)
	public void jsonDumpPrivateView() throws JsonProcessingException {
		// when
		String jsonStr = StrUtils.jsonDumpPrivateView(testUser);

		FakeUser deserializedUser = StrUtils.objectMapper.readValue(jsonStr, FakeUser.class);

		// then
		assertThat(deserializedUser).isNotNull();
		assertThat(deserializedUser.getPassword()).isEqualTo(testUser.getPassword());
	}

	@Test
	@Order(20)
	public void jsonDumpPublicView() throws JsonProcessingException {
		// when
		String jsonStr = StrUtils.jsonDumpPublicView(testUser);

		FakeUser deserializedUser = StrUtils.objectMapper.readValue(jsonStr, FakeUser.class);

		// then
		assertThat(deserializedUser).isNotNull();
		assertThat(deserializedUser.getPassword()).isNull();
	}
}
