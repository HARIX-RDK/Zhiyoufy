package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateUpdateParam;
import com.example.zhiyoufy.server.service.impl.PmsJobTemplateServiceImpl;
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
public class PmsJobTemplateServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	PmsProjectService projectService;

	@Autowired
	PmsJobTemplateService jobTemplateService;

	@Autowired
	PmsJobTemplateServiceImpl jobTemplateServiceImpl;
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
	public void add_job_template_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		String folderName = serviceTestHelper.getDefaultJobFolderName();

		PmsJobFolder jobFolder = serviceTestHelper.getJobFolderByProjectAndName(project, folderName);

		String jobTemplateName = "new.jobTemplate";
		PmsJobTemplateParam jobTemplateParam = PmsJobTemplateParam.builder()
				.projectId(project.getId())
				.projectName(project.getName())
				.folderId(jobFolder.getId())
				.folderName(jobFolder.getName())
				.name(jobTemplateName)
				.jobPath("job_path")
				.timeoutSeconds(1800)
				.build();

		// when
		PmsJobTemplate jobTemplate = jobTemplateService.addJobTemplate(jobTemplateParam);

		// then
		assertThat(jobTemplate).isNotNull();

		log.debug("jobTemplate {}", StrUtils.jsonDump(jobTemplate));

		assertThat(jobTemplate.getName()).isEqualTo(jobTemplateName);
	}

	@Test
	@Order(200)
	public void del_job_template_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobTemplateName = serviceTestHelper.getDefaultJobTemplateName();

		// when
		PmsJobTemplate jobTemplate =
				jobTemplateService.getJobTemplateByProjectIdAndName(projectId, jobTemplateName);

		// then
		assertThat(jobTemplate).isNotNull();

		log.debug("jobTemplate {}", StrUtils.jsonDump(jobTemplate));

		assertThat(jobTemplate.getName()).isEqualTo(jobTemplateName);

		// given
		Long jobTemplateId = jobTemplate.getId();

		// when
		DeleteInfo deleteInfo = jobTemplateService.delJobTemplateById(jobTemplateId);

		// then
		assertThat(deleteInfo).isNotNull();

		log.debug("deleteInfo {}", StrUtils.jsonDump(deleteInfo));

		assertThat(deleteInfo.getDeleted()).isEqualTo(1);
		assertThat(deleteInfo.getName()).isEqualTo(jobTemplateName);
	}

	@Test
	@Order(300)
	public void get_job_template_list_without_folderId_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();

		PmsJobTemplateQueryParam queryParam = new PmsJobTemplateQueryParam();
		queryParam.setKeyword("JobTemplate_001");

		// when
		CommonPage<PmsJobTemplate> jobTemplatePage =
				jobTemplateService.getJobTemplateList(projectId, queryParam, 20, 1);

		// then
		assertThat(jobTemplatePage).isNotNull();

		log.debug("jobTemplatePage {}", StrUtils.jsonDump(jobTemplatePage));

		assertThat(jobTemplatePage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(310)
	public void get_job_template_list_with_folderId_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		String folderName = serviceTestHelper.getDefaultJobFolderName();

		PmsJobFolder jobFolder = serviceTestHelper.getJobFolderByProjectAndName(project, folderName);

		Long projectId = project.getId();

		PmsJobTemplateQueryParam queryParam = new PmsJobTemplateQueryParam();
		queryParam.setKeyword("JobTemplate_001");
		queryParam.setFolderId(jobFolder.getId());

		// when
		CommonPage<PmsJobTemplate> jobTemplatePage =
				jobTemplateService.getJobTemplateList(projectId, queryParam, 20, 1);

		// then
		assertThat(jobTemplatePage).isNotNull();

		log.debug("jobTemplatePage {}", StrUtils.jsonDump(jobTemplatePage));

		assertThat(jobTemplatePage.getList().size()).isEqualTo(1);
	}

	@Test
	@Order(400)
	public void update_job_template_should_be_ok() {
		serviceTestHelper.formLoginSet001();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		Long projectId = project.getId();
		String jobTemplateName = serviceTestHelper.getDefaultJobTemplateName();
		String jobTemplateUpdatedDescription = "# update_job_template_should_be_ok";

		PmsJobTemplate jobTemplate =
				jobTemplateService.getJobTemplateByProjectIdAndName(projectId, jobTemplateName);

		PmsJobTemplateUpdateParam updateParam = PmsJobTemplateUpdateParam.builder()
				.description(jobTemplateUpdatedDescription)
				.build();

		// when
		UpdateInfo updateInfo = jobTemplateService.updateJobTemplate(jobTemplate.getId(), updateParam);

		// then
		assertThat(updateInfo).isNotNull();
		assertThat(updateInfo.getUpdated()).isEqualTo(1);
		assertThat(updateInfo.getName()).isEqualTo(jobTemplateName);

		// when
		PmsJobTemplate jobTemplateUpdated =
				jobTemplateService.getJobTemplateById(jobTemplate.getId());

		// then
		assertThat(jobTemplateUpdated).isNotNull();

		log.debug("jobTemplateUpdated {}", StrUtils.jsonDump(jobTemplateUpdated));

		assertThat(jobTemplateUpdated.getDescription()).isEqualTo(jobTemplateUpdatedDescription);
	}
}
