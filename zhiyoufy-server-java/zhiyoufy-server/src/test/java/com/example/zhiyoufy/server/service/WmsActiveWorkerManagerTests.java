package com.example.zhiyoufy.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceReq;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceRsp;
import com.example.zhiyoufy.server.domain.bo.wms.WmsFreeJobResourceReq;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
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
import org.springframework.test.annotation.DirtiesContext;
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
public class WmsActiveWorkerManagerTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	WmsWorkerAppService workerAppService;
	@Autowired
	WmsWorkerGroupService workerGroupService;

	@Autowired
	ActiveWorkerManager activeWorkerManager;
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
	@DirtiesContext
	@Order(100)
	public void add_active_worker_then_alloc_then_free_should_be_ok() {
		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		int maxActiveJobNum = 50;

		WmsWorkerRegisterParam registerParam = new WmsWorkerRegisterParam();
		registerParam.setWorkerApp(workerAppName);
		registerParam.setWorkerGroup(workerGroupName);
		registerParam.setGroupTokenName("test-placeholder");
		registerParam.setAppRunId(RandomUtils.generateShortHexId());
		registerParam.setAppStartTimestamp(new Date());
		registerParam.setWorkerName(serviceTestHelper.getDefaultWorkerName());
		registerParam.setMaxActiveJobNum(maxActiveJobNum);

		String sessionId = RandomUtils.generateHexId();

		WmsActiveWorkerBase activeWorkerBase = WmsWorkerAppStructMapper.INSTANCE
				.registerParamToWmsActiveWorkerBase(registerParam);
		activeWorkerBase.setSessionId(sessionId);
		activeWorkerBase.setConnectTime(new Date());
		activeWorkerBase.setFreeActiveJobNum(activeWorkerBase.getMaxActiveJobNum());

		WmsActiveWorker activeWorker = new WmsActiveWorker();
		activeWorker.setActiveWorkerBase(activeWorkerBase);

		activeWorker.setWorkerApp(workerApp);
		activeWorker.setWorkerGroup(workerGroup);
		activeWorker.setRegisterParam(registerParam);

		// when
		activeWorkerManager.onActiveWorkerRegister(activeWorker);

		List<WmsWorkerAppBase>  appBaseList = activeWorkerManager.getAppBaseList();

		List<WmsActiveWorkerGroupBase> groupBaseList =
				activeWorkerManager.getGroupBaseListByAppId(workerApp.getId());

		// then
		assertThat(appBaseList).isNotNull();

		log.debug("appBaseList {}", StrUtils.jsonDump(appBaseList));

		assertThat(appBaseList.size()).isEqualTo(1);
		assertThat(appBaseList.get(0).getName()).isEqualTo(workerAppName);

		assertThat(groupBaseList).isNotNull();

		log.debug("groupBaseList {}", StrUtils.jsonDump(groupBaseList));

		assertThat(groupBaseList.size()).isEqualTo(1);
		assertThat(groupBaseList.get(0).getName()).isEqualTo(workerGroupName);
		assertThat(groupBaseList.get(0).getFreeActiveJobNum()).isEqualTo(maxActiveJobNum);

		// given
		String runGuid = RandomUtils.generateHexId();
		int parallelNum = 5;

		WmsAllocJobResourceReq allocReq = new WmsAllocJobResourceReq();
		allocReq.setAppId(workerApp.getId());
		allocReq.setGroupId(workerGroup.getId());

		List<WmsActiveJobBase> activeJobBaseList = new ArrayList<>();
		allocReq.setActiveJobBaseList(activeJobBaseList);

		for (int i = 0; i < parallelNum; i++) {
			WmsActiveJobBase activeJobBase = new WmsActiveJobBase();
			activeJobBase.setRunGuid(runGuid);
			activeJobBase.setIdx(i);

			activeJobBaseList.add(activeJobBase);
		}

		// when
		WmsAllocJobResourceRsp allocRsp = activeWorkerManager.allocJobResource(allocReq);

		// then
		assertThat(allocRsp).isNotNull();
		assertThat(allocRsp.getJobKeyToWorker().size()).isEqualTo(parallelNum);

		assertThat(groupBaseList.get(0).getFreeActiveJobNum()).isEqualTo(maxActiveJobNum - parallelNum);

		WmsActiveWorker allocedActiveWorker =
				allocRsp.getJobKeyToWorker().values().stream().findAny().get();

		assertThat(allocedActiveWorker.getActiveWorkerBase().getFreeActiveJobNum())
				.isEqualTo(maxActiveJobNum - parallelNum);


		for (int i = 0; i < parallelNum; i++) {
			// given
			WmsActiveJobBase activeJobBase = activeJobBaseList.get(i);
			String jobKey = activeJobBase.getJobKey();

			Map<String, WmsActiveWorker> jobKeyToWorker = new HashMap<>();
			WmsActiveWorker workerToFree = allocRsp.getJobKeyToWorker().get(jobKey);
			jobKeyToWorker.put(jobKey, workerToFree);

			WmsFreeJobResourceReq freeReq = new WmsFreeJobResourceReq();
			freeReq.setAppId(workerApp.getId());
			freeReq.setGroupId(workerGroup.getId());
			freeReq.setJobKeyToWorker(jobKeyToWorker);

			// when
			activeWorkerManager.freeJobResource(freeReq);

			groupBaseList =
					activeWorkerManager.getGroupBaseListByAppId(workerApp.getId());

			// then
			assertThat(groupBaseList.get(0).getFreeActiveJobNum())
					.isEqualTo(maxActiveJobNum - parallelNum + i + 1);

			assertThat(workerToFree.getActiveWorkerBase().getFreeActiveJobNum())
					.isEqualTo(maxActiveJobNum - parallelNum + i + 1);
		}
	}
}
