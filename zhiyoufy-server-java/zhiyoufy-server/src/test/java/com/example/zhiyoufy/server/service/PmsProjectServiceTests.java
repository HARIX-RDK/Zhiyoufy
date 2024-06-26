package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectBase;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectQueryParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import com.example.zhiyoufy.server.service.impl.PmsProjectServiceImpl;
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
public class PmsProjectServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	PmsProjectService projectService;

	@Autowired
	PmsProjectServiceImpl projectServiceImpl;
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
	public void add_project_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String projectName = "new.project";
		PmsProjectParam projectParam = PmsProjectParam.builder()
				.name(projectName)
				.build();

		// when
		PmsProject pmsProject = projectService.addProject(projectParam);

		// then
		assertThat(pmsProject).isNotNull();

		log.debug("pmsProject {}", StrUtils.jsonDump(pmsProject));

		assertThat(pmsProject.getName()).isEqualTo(projectName);
	}

	@Test
	@Order(200)
	public void del_project_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();;

		// when
		PmsProject pmsProject = projectService.getProjectByName(projectName);

		// then
		assertThat(pmsProject).isNotNull();

		log.debug("pmsProject {}", StrUtils.jsonDump(pmsProject));

		assertThat(pmsProject.getName()).isEqualTo(projectName);

		// given
		Long projectId = pmsProject.getId();

		// when
		int deleted = projectService.delProjectById(projectId);

		// then
		assertThat(deleted).isEqualTo(1);
	}

	@Test
	@Order(300)
	public void get_project_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		CommonPage<PmsProjectFull> projectPage = getProjectList("p");

		// then
		assertThat(projectPage).isNotNull();

		assertThat(projectPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(310)
	public void get_project_base_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		List<PmsProjectBase> projectBaseList =
				projectService.getProjectBaseList();

		// then
		assertThat(projectBaseList).isNotNull();

		log.debug("projectBaseList {}", StrUtils.jsonDump(projectBaseList));

		assertThat(projectBaseList.size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_project_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();;

		// when
		PmsProject pmsProject = projectService.getProjectByName(projectName);

		// then
		assertThat(pmsProject).isNotNull();

		log.debug("pmsProject {}", StrUtils.jsonDump(pmsProject));

		assertThat(pmsProject.getName()).isEqualTo(projectName);

		// given
		Long projectId = pmsProject.getId();
		String newName = projectName + "_updated";
		PmsProjectUpdateParam updateParam = new PmsProjectUpdateParam();
		updateParam.setName(newName);

		// when
		int updated = projectService.updateProject(projectId, updateParam);

		// then
		assertThat(updated).isEqualTo(1);

		// when
		pmsProject = projectService.getProjectById(projectId);

		// then
		assertThat(pmsProject).isNotNull();

		log.debug("pmsProject {}", StrUtils.jsonDump(pmsProject));

		assertThat(pmsProject.getName()).isEqualTo(newName);
	}

	@Test
	@Order(1000)
	public void add_project_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSwe001 = serviceTestHelper.getUserByName("swe001");

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		PmsProjectUserRelationFull relationFull = new PmsProjectUserRelationFull();
		relationFull.setProjectId(project.getId());
		relationFull.setUserId(userSwe001.getId());
		relationFull.setUsername(userSwe001.getUsername());
		relationFull.setIsOwner(false);
		relationFull.setIsEditor(true);

		// when
		int cnt = projectService.addProjectUserRelation(relationFull);

		// then
		assertThat(cnt).isEqualTo(1);

		// when
		CommonPage<PmsProjectUserRelationFull> relationPage =
				projectService.getProjectUserRelationListByProjectId(
						project.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(1010)
	public void del_project_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSet001 = serviceTestHelper.getUserByName("set001");

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		// when
		CommonPage<PmsProjectUserRelationFull> relationPage =
				projectService.getProjectUserRelationListByProjectId(
						project.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);

		PmsProjectUserRelationFull relationFull = relationPage.getList().stream()
				.filter(relation -> {
					return relation.getUsername().equals(userSet001.getUsername());
				})
				.findAny()
				.orElse(null);

		assertThat(relationFull).isNotNull();

		// when
		int cnt = projectService.delProjectUserRelationById(relationFull.getId());

		// then
		assertThat(cnt).isEqualTo(1);
	}

	@Test
	@Order(1020)
	public void get_project_user_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String projectName = serviceTestHelper.getDefaultProjectName();

		PmsProject project = serviceTestHelper.getProjectByName(projectName);

		// when
		CommonPage<PmsProjectUserRelationFull> relationPage =
				projectService.getProjectUserRelationListByProjectId(
						project.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(0);
	}

	private CommonPage<PmsProjectFull> getProjectList(String keyword) {
		// given
		PmsProjectQueryParam queryParam = new PmsProjectQueryParam();
		queryParam.setKeyword(keyword);
		queryParam.setSortBy("name");

		// when
		CommonPage<PmsProjectFull> projectPage = projectService.getProjectList(
				queryParam, 20, 1);

		// then
		assertThat(projectPage).isNotNull();

		log.debug("projectPage {}", StrUtils.jsonDump(projectPage));

		return projectPage;
	}
}
