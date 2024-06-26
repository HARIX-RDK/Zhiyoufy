package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.service.impl.EmsEnvironmentLockServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsEnvironmentServiceImpl;
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
public class EmsEnvironmentLockServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	EmsEnvironmentLockService environmentLockService;
	//endregion

	//region setup & cleanup
	@BeforeEach
	public void setup(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		environmentLockService = new EmsEnvironmentLockServiceImpl();
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
	public void try_lock_free_env_ids_should_be_ok() {
		// given
		List<Long> envIds = List.of(1L, 2L, 3L);

		// when
		boolean locked = environmentLockService.tryLock(envIds, 1000);

		// then
		assertThat(locked).isTrue();
	}

	@Test
	@Order(200)
	public void try_lock_busy_env_ids_should_fail() {
		// given
		List<Long> envIds = List.of(1L, 2L, 3L);

		// when
		boolean lockedFirst = environmentLockService.tryLock(envIds, 1000);
		long curTime = System.currentTimeMillis();
		boolean lockedSecond = environmentLockService.tryLock(envIds, 1000);
		long costTime = System.currentTimeMillis() - curTime;

		log.debug("costTime {}", costTime);

		// then
		assertThat(lockedFirst).isTrue();
		assertThat(lockedSecond).isFalse();

		assertThat(costTime).isGreaterThanOrEqualTo(1000);
	}

	@Test
	@Order(300)
	public void try_lock_busy_env_ids_later_unlocked_should_be_ok() {
		// given
		List<Long> envIds = List.of(1L, 2L, 3L);

		// when
		boolean lockedFirst = environmentLockService.tryLock(envIds, 1000);
		boolean lockedSecond = environmentLockService.tryLock(envIds, 1000);

		// then
		assertThat(lockedFirst).isTrue();
		assertThat(lockedSecond).isFalse();

		// given
		long curTime = System.currentTimeMillis();

		// when
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			environmentLockService.unlock(envIds);
		});

		thread.start();

		boolean lockedThird = environmentLockService.tryLock(envIds, 2000);
		long costTime = System.currentTimeMillis() - curTime;

		log.debug("costTime {}", costTime);

		// then
		assertThat(lockedThird).isTrue();
		assertThat(costTime).isGreaterThanOrEqualTo(1000);
	}
}
