package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobRun;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
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
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ServiceTestGroupGeneralConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Transactional
@Slf4j
public class JmsActiveJobRunManagerTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	ActiveJobRunManager activeJobRunManager;
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
	public void start_job_run_should_be_ok() {
		serviceTestHelper.registerDefaultWorker();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);
		log.info("workerGroup.worker_labels={}", workerGroup.getWorkerLabels());
		String envName = serviceTestHelper.getDefaultEnvName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);
        log.info("environment.worker_labels={}", environment.getWorkerLabels());
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobTemplateName = serviceTestHelper.getDefaultJobTemplateName();

		PmsJobTemplate jobTemplate =
				serviceTestHelper.getJobTemplateByProjectIdAndName(projectId, jobTemplateName);

		String runId = RandomUtils.generateHexId();
		int runNum = 5;
		int parallelNum = 5;
		String runTag = "for_test";

		JmsStartJobRunParam startJobRunParam = new JmsStartJobRunParam();
		startJobRunParam.setRunGuid(runId);
		startJobRunParam.setWorkerAppId(workerApp.getId());
		startJobRunParam.setWorkerAppName(workerAppName);
		startJobRunParam.setWorkerGroupId(workerGroup.getId());
		startJobRunParam.setWorkerGroupName(workerGroupName);
		startJobRunParam.setEnvironmentId(environment.getId());
		startJobRunParam.setEnvironmentName(envName);
		startJobRunParam.setTemplateId(jobTemplate.getId());
		startJobRunParam.setTemplateName(jobTemplateName);
		startJobRunParam.setRunTag(runTag);
		startJobRunParam.setRunNum(runNum);
		startJobRunParam.setParallelNum(parallelNum);

		// when
		JmsActiveJobRun activeJobRun = activeJobRunManager.startJobRun(startJobRunParam);

		// then
		assertThat(activeJobRun).isNotNull();
	}

	@Test
	@DirtiesContext
	@Order(100)
	public void start_job_run_exceed_config_resource_should_fail() {
		serviceTestHelper.registerDefaultWorker();

		// given
		String workerAppName = serviceTestHelper.getDefaultWorkerAppName();

		WmsWorkerApp workerApp = serviceTestHelper.getWorkerAppByName(workerAppName);

		String workerGroupName = serviceTestHelper.getDefaultWorkerGroupName();

		WmsWorkerGroup workerGroup = serviceTestHelper.getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		String envName = serviceTestHelper.getDefaultEnvName();

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobTemplateName = serviceTestHelper.getDefaultJobTemplateName();

		PmsJobTemplate jobTemplate =
				serviceTestHelper.getJobTemplateByProjectIdAndName(projectId, jobTemplateName);

		String runId = RandomUtils.generateHexId();
		int runNum = 10;
		int parallelNum = 10;
		String runTag = "for_test";

		JmsStartJobRunParam startJobRunParam = new JmsStartJobRunParam();
		startJobRunParam.setRunGuid(runId);
		startJobRunParam.setWorkerAppId(workerApp.getId());
		startJobRunParam.setWorkerAppName(workerAppName);
		startJobRunParam.setWorkerGroupId(workerGroup.getId());
		startJobRunParam.setWorkerGroupName(workerGroupName);
		startJobRunParam.setEnvironmentId(environment.getId());
		startJobRunParam.setEnvironmentName(envName);
		startJobRunParam.setTemplateId(jobTemplate.getId());
		startJobRunParam.setTemplateName(jobTemplateName);
		startJobRunParam.setRunTag(runTag);
		startJobRunParam.setRunNum(runNum);
		startJobRunParam.setParallelNum(parallelNum);

		// when
		Throwable thrown = catchThrowable(
				() -> activeJobRunManager.startJobRun(startJobRunParam));

		// then
		assertThat(thrown).isNotNull();
	}
}
