package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderUpdateParam;
import com.example.zhiyoufy.server.service.impl.PmsJobFolderServiceImpl;
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
public class PmsJobFolderServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	PmsProjectService projectService;

	@Autowired
	PmsJobFolderService jobFolderService;

	@Autowired
	PmsJobFolderServiceImpl jobFolderServiceImpl;
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
	public void add_job_folder_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		String jobFolderName = "new.jobFolder";
		PmsJobFolderParam jobFolderParam = PmsJobFolderParam.builder()
				.projectId(project.getId())
				.projectName(project.getName())
				.parentId(0L)
				.parentName("root")
				.name(jobFolderName)
				.build();

		// when
		PmsJobFolder jobFolder = jobFolderService.addJobFolder(jobFolderParam);

		// then
		assertThat(jobFolder).isNotNull();

		log.debug("jobFolder {}", StrUtils.jsonDump(jobFolder));

		assertThat(jobFolder.getName()).isEqualTo(jobFolderName);
	}

	@Test
	@Order(200)
	public void del_job_folder_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobFolderName = serviceTestHelper.getDefaultJobFolderName();

		// when
		PmsJobFolder jobFolder =
				jobFolderService.getJobFolderByProjectIdAndName(projectId, jobFolderName);

		// then
		assertThat(jobFolder).isNotNull();

		log.debug("jobFolder {}", StrUtils.jsonDump(jobFolder));

		assertThat(jobFolder.getName()).isEqualTo(jobFolderName);

		// given
		Long jobFolderId = jobFolder.getId();

		// when
		DeleteInfo deleteInfo = jobFolderService.delJobFolderById(jobFolderId);

		// then
		assertThat(deleteInfo).isNotNull();

		log.debug("deleteInfo {}", StrUtils.jsonDump(deleteInfo));

		assertThat(deleteInfo.getDeleted()).isEqualTo(2);
		assertThat(deleteInfo.getName()).isEqualTo(jobFolderName);
	}

	@Test
	@Order(300)
	public void get_job_folder_list_without_parentId_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();

		PmsJobFolderQueryParam queryParam = new PmsJobFolderQueryParam();
		queryParam.setKeyword("JobFolder_001");

		// when
		CommonPage<PmsJobFolder> jobFolderPage =
				jobFolderService.getJobFolderList(projectId, queryParam, 20, 1);

		// then
		assertThat(jobFolderPage).isNotNull();

		log.debug("jobFolderPage {}", StrUtils.jsonDump(jobFolderPage));

		assertThat(jobFolderPage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(310)
	public void get_job_folder_list_with_parentId_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();

		PmsJobFolderQueryParam queryParam = new PmsJobFolderQueryParam();
		queryParam.setKeyword("JobFolder_001");
		queryParam.setParentId(0L);

		// when
		CommonPage<PmsJobFolder> jobFolderPage =
				jobFolderService.getJobFolderList(projectId, queryParam, 20, 1);

		// then
		assertThat(jobFolderPage).isNotNull();

		log.debug("jobFolderPage {}", StrUtils.jsonDump(jobFolderPage));

		assertThat(jobFolderPage.getList().size()).isEqualTo(1);
	}

	@Test
	@Order(400)
	public void update_job_folder_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobFolderName = serviceTestHelper.getDefaultJobFolderName();
		String jobFolderUpdatedDescription = "# update_job_folder_should_be_ok";

		PmsJobFolder jobFolder =
				jobFolderService.getJobFolderByProjectIdAndName(projectId, jobFolderName);

		PmsJobFolderUpdateParam updateParam = PmsJobFolderUpdateParam.builder()
				.description(jobFolderUpdatedDescription)
				.build();

		// when
		UpdateInfo updateInfo = jobFolderService.updateJobFolder(jobFolder.getId(), updateParam);

		// then
		assertThat(updateInfo).isNotNull();
		assertThat(updateInfo.getUpdated()).isEqualTo(1);
		assertThat(updateInfo.getName()).isEqualTo(jobFolderName);

		// when
		PmsJobFolder jobFolderUpdated =
				jobFolderService.getJobFolderById(jobFolder.getId());

		// then
		assertThat(jobFolderUpdated).isNotNull();

		log.debug("jobFolderUpdated {}", StrUtils.jsonDump(jobFolderUpdated));

		assertThat(jobFolderUpdated.getDescription()).isEqualTo(jobFolderUpdatedDescription);
	}
}
