package com.example.zhiyoufy.server.support.service;

import java.util.Date;

import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.dto.ums.FormLoginParam;
import com.example.zhiyoufy.server.domain.dto.ums.LoginResponseData;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import com.example.zhiyoufy.server.elasticsearch.JmsJobRunResultFullRepository;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
import com.example.zhiyoufy.server.security.UmsAuthenticationToken;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import com.example.zhiyoufy.server.service.PmsJobFolderService;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
import com.example.zhiyoufy.server.service.PmsProjectService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

@Getter
@Setter
@Slf4j
public class ServiceTestHelper implements InitializingBean {
	@Autowired
	UmsUserService userService;

	@Autowired
	EmsEnvironmentService environmentService;

	@Autowired
	EmsConfigCollectionService configCollectionService;

	@Autowired
	PmsProjectService projectService;

	@Autowired
	PmsJobFolderService jobFolderService;

	@Autowired
	PmsJobTemplateService jobTemplateService;

	@Autowired
	WmsWorkerAppService workerAppService;

	@Autowired
	WmsWorkerGroupService workerGroupService;

	@Autowired
	ActiveWorkerManager activeWorkerManager;

	@Autowired
	ZhiyoufyTestProperties zhiyoufyTestProperties;

	@Autowired
	JmsJobRunResultFullRepository jmsJobRunResultFullRepository;


	String adminPassword;
	String defaultEnvName = "env_for_configs";
	String defaultCollectionName = "ConfigCollection_001";
	String defaultItemName = "ConfigItem_001";
	String defaultProjectName = "project_001";
	String defaultJobFolderName = "JobFolder_001";
	String defaultJobTemplateName = "JobTemplate_001";
	String defaultWorkerAppName = "WorkerApp_001";
	String defaultWorkerGroupName = "WorkerGroup_001";
	String defaultGroupTokenName = "GroupToken_001";
	String defaultWorkerName = "Worker_001";

	@Override
	public void afterPropertiesSet() throws Exception {
		adminPassword = zhiyoufyTestProperties.getAdminPassword();
	}

	public void formLoginAdmin() {
		formLogin("admin", adminPassword);
	}

	public void formLoginSet001() {
		formLogin("set001", adminPassword);
	}

	public void formLoginSysadmin() {
		formLogin("sysadmin", adminPassword);
	}

	public void formLogin(String username, String password) {
		// given
		FormLoginParam loginParam = FormLoginParam.builder()
				.username(username)
				.password(password)
				.build();

		// when
		LoginResponseData loginResponseData = userService.formLogin(loginParam);

		// then
		assertThat(loginResponseData).isNotNull();
		assertThat(loginResponseData.getToken()).isNotNull();

		log.debug("loginResponseData {}", StrUtils.jsonDump(loginResponseData));

		// given
		String token = loginResponseData.getToken();

		// when
		UmsUserDetails userDetails = userService.loadUserDetailsByToken(token);

		// then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUmsUser().getUsername()).isEqualTo(username);

		UmsAuthenticationToken authentication = new UmsAuthenticationToken(userDetails, token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void clearAuthentication() {
		SecurityContextHolder.clearContext();
	}

	public UmsUser getUserByName(String username) {
		// when
		UmsUser umsUser = userService.getUserByUsername(username);

		// then
		assertThat(umsUser).isNotNull();

		return umsUser;
	}

	public EmsEnvironment getEnvironmentByName(String envName) {
		// when
		EmsEnvironment environment = environmentService.getEnvironmentByName(envName);

		// then
		assertThat(environment).isNotNull();

		return environment;
	}

	public EmsConfigCollection getConfigCollectionByName(String envName, String collectionName) {
		// when
		EmsEnvironment environment = getEnvironmentByName(envName);

		EmsConfigCollection configCollection =
				configCollectionService.getConfigCollectionByEnvIdAndName(
						environment.getId(), collectionName);

		// then
		assertThat(configCollection).isNotNull();

		return configCollection;
	}

	public EmsConfigCollection getConfigCollectionByEnvAndName(
			EmsEnvironment environment, String collectionName) {
		// when
		EmsConfigCollection configCollection =
				configCollectionService.getConfigCollectionByEnvIdAndName(
						environment.getId(), collectionName);

		// then
		assertThat(configCollection).isNotNull();

		return configCollection;
	}

	public PmsProject getProjectByName(String projectName) {
		// when
		PmsProject project = projectService.getProjectByName(projectName);

		// then
		assertThat(project).isNotNull();

		return project;
	}

	public PmsJobFolder getJobFolderByProjectAndName(
			PmsProject project, String folderName) {
		// when
		PmsJobFolder jobFolder = jobFolderService.getJobFolderByProjectIdAndName(
				project.getId(), folderName);

		// then
		assertThat(jobFolder).isNotNull();

		return jobFolder;
	}

	public PmsJobTemplate getJobTemplateByProjectIdAndName(Long projectId,
			String jobTemplateName) {
		PmsJobTemplate jobTemplate =
				jobTemplateService.getJobTemplateByProjectIdAndName(projectId, jobTemplateName);

		assertThat(jobTemplate).isNotNull();

		return jobTemplate;
	}

	public WmsWorkerApp getWorkerAppByName(String workerAppName) {
		// when
		WmsWorkerApp workerApp = workerAppService.getWorkerAppByName(workerAppName);

		// then
		assertThat(workerApp).isNotNull();

		return workerApp;
	}

	public WmsWorkerGroup getWorkerGroupByWorkerAppAndName(WmsWorkerApp workerApp,
			String workerGroupName) {
		// when
		WmsWorkerGroup workerGroup =
				workerGroupService.getWorkerGroupByWorkerAppIdAndName(
						workerApp.getId(), workerGroupName);

		// then
		assertThat(workerGroup).isNotNull();

		return workerGroup;
	}

	public void registerDefaultWorker() {
		String workerAppName = getDefaultWorkerAppName();

		WmsWorkerApp workerApp = getWorkerAppByName(workerAppName);

		String workerGroupName = getDefaultWorkerGroupName();

		WmsWorkerGroup workerGroup = getWorkerGroupByWorkerAppAndName(
				workerApp, workerGroupName);

		int maxActiveJobNum = 50;

		WmsWorkerRegisterParam registerParam = new WmsWorkerRegisterParam();
		registerParam.setWorkerApp(workerAppName);
		registerParam.setWorkerGroup(workerGroupName);
		registerParam.setGroupTokenName("test-placeholder");
		registerParam.setAppRunId(RandomUtils.generateShortHexId());
		registerParam.setAppStartTimestamp(new Date());
		registerParam.setWorkerName(defaultWorkerName);
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

		activeWorkerManager.onActiveWorkerRegister(activeWorker);
	}
}
