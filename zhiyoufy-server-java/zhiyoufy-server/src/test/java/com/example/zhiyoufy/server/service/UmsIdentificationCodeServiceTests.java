package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.server.config.DataSourceBaseConfig;
import com.example.zhiyoufy.server.config.ServiceTestConfig;
import com.example.zhiyoufy.server.service.impl.UmsIdentificationCodeServiceImpl;
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
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({DataSourceBaseConfig.class, ServiceTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Slf4j
public class UmsIdentificationCodeServiceTests {
	UmsIdentificationCodeServiceImpl umsIdentificationCodeService;

	private long curTime;

	@BeforeEach
	public void setup(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		umsIdentificationCodeService = new UmsIdentificationCodeServiceImpl();

		curTime = 0;
		umsIdentificationCodeService.setTimeSupplier(this::getCurrentTime);
	}

	@AfterEach
	public void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);
	}

	@Test
	@Order(10)
	public void generateIdentificationCode_should_be_ok() {
		// given
		String idKey = "cicd@example.com";

		// when
		String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);
	}

	@Test
	@Order(11)
	public void generateIdentificationCode_too_frequent_should_fail() {
		// given
		String idKey = "cicd@example.com";

		// when
		String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);

		// when
		Throwable thrown = catchThrowable(
				() -> umsIdentificationCodeService.generateIdentificationCode(idKey));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(12)
	public void generateIdentificationCode_after_enough_long_should_ok() {
		// given
		String idKey = "cicd@example.com";

		// when
		String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);

		// given
		curTime += 60L * 1000_000_000;

		// when
		code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);
	}

	@Test
	@Order(13)
	public void generateIdentificationCode_exeed_max_live_should_fail() {
		// given
		String idKeyPrefix = "idKeyPrefix-";

		for (int i = 0; i < umsIdentificationCodeService.getMaxLiveCode(); i++) {
			String idKey = idKeyPrefix + i;
			umsIdentificationCodeService.generateIdentificationCode(idKey);
		}

		String exceedIdKey = "idKey-exceed";

		// when
		Throwable thrown = catchThrowable(
				() -> umsIdentificationCodeService.generateIdentificationCode(exceedIdKey));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(20)
	public void getIdentificationCode_in_time_should_match() {
		// given
		String idKey = "cicd@example.com";

		// when
		String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);

		// given
		curTime += 10L * 1000_000_000;

		// when
		String gotCode = umsIdentificationCodeService.getIdentificationCode(idKey);

		log.debug("got code {} for {}", code, idKey);

		// then
		assertThat(gotCode).isNotNull();
		assertThat(gotCode).isEqualTo(code);
	}

	@Test
	@Order(21)
	public void getIdentificationCode_late_should_return_null() {
		// given
		String idKey = "cicd@example.com";

		// when
		String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

		log.debug("generated code {} for {}", code, idKey);

		// then
		assertThat(code).isNotNull();
		assertThat(code.length()).isEqualTo(8);

		// given
		curTime += umsIdentificationCodeService.getExpireAfterInterval();

		// when
		String gotCode = umsIdentificationCodeService.getIdentificationCode(idKey);

		log.debug("got code {} for {}", gotCode, idKey);

		// then
		assertThat(gotCode).isNull();
	}

	public long getCurrentTime() {
		return curTime;
	}
}
