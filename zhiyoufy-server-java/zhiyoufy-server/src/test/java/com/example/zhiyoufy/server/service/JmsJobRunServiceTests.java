package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobRun;
import com.example.zhiyoufy.server.support.service.ServiceTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public class JmsJobRunServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	JmsJobRunService jobRunService;
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
	@Disabled
	@Order(100)
	public void get_job_run_result_list_should_be_ok() {
		// given
		long projectId = 2;

		Long environmentId = null;
		String templateName = null;

		environmentId = 2L;
		templateName = "zhiyoufy_test_exclude_preparation";

		JmsJobRunResultQueryParam queryParam = new JmsJobRunResultQueryParam();
		queryParam.setProjectId(projectId);
		queryParam.setEnvironmentId(environmentId);
		queryParam.setTemplateName(templateName);

		// when
		CommonPage<JmsJobRunResultFull> page =
				jobRunService.getJobRunResultList(queryParam, 20, 1);

		// then
		assertThat(page).isNotNull();

		log.debug("page {}", StrUtils.jsonDump(page));
	}
}
