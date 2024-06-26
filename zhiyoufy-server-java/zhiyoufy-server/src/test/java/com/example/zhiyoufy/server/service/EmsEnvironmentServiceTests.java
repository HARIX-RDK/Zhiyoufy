package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentBase;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
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
public class EmsEnvironmentServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	EmsEnvironmentService environmentService;

	@Autowired
	EmsEnvironmentServiceImpl environmentServiceImpl;
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
	public void add_environment_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String envName = "new.env";
		EmsEnvironmentParam environmentParam = EmsEnvironmentParam.builder()
				.name(envName)
				.build();

		// when
		EmsEnvironment emsEnvironment = environmentService.addEnvironment(environmentParam);

		// then
		assertThat(emsEnvironment).isNotNull();

		log.debug("emsEnvironment {}", StrUtils.jsonDump(emsEnvironment));

		assertThat(emsEnvironment.getName()).isEqualTo(envName);
	}

	@Test
	@Order(110)
	public void add_environment_with_parent_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		EmsEnvironment parentEnv = environmentService.getEnvironmentByName("env_001");

		// then
		assertThat(parentEnv).isNotNull();

		// given
		String envName = "new.env";
		EmsEnvironmentParam environmentParam = EmsEnvironmentParam.builder()
				.parentId(parentEnv.getId())
				.parentName(parentEnv.getName())
				.name(envName)
				.build();

		// when
		EmsEnvironment emsEnvironment = environmentService.addEnvironment(environmentParam);

		// then
		assertThat(emsEnvironment).isNotNull();

		log.debug("emsEnvironment {}", StrUtils.jsonDump(emsEnvironment));

		assertThat(emsEnvironment.getName()).isEqualTo(envName);
	}

	@Test
	@Order(200)
	public void del_environment_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String envName = "env_001";
		String childEnvName = "env_001_001";

		// when
		EmsEnvironment emsEnvironment = environmentService.getEnvironmentByName(envName);
		EmsEnvironment childEnv = environmentService.getEnvironmentByName(childEnvName);

		// then
		assertThat(emsEnvironment).isNotNull();

		log.debug("emsEnvironment {}", StrUtils.jsonDump(emsEnvironment));

		assertThat(emsEnvironment.getName()).isEqualTo(envName);

		assertThat(childEnv).isNotNull();

		log.debug("childEnv {}", StrUtils.jsonDump(childEnv));

		assertThat(childEnv.getName()).isEqualTo(childEnvName);

		// given
		Long envId = emsEnvironment.getId();

		// when
		int deleted = environmentService.delEnvironmentById(envId);

		// then
		assertThat(deleted).isEqualTo(1);

		// when
		childEnv = environmentService.getEnvironmentByName(childEnvName);

		// then
		assertThat(childEnv).isNotNull();

		log.debug("childEnv {}", StrUtils.jsonDump(childEnv));

		assertThat(childEnv.getParentId()).isEqualTo(0L);
	}

	@Test
	@Order(300)
	public void get_environment_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		CommonPage<EmsEnvironmentFull> envPage = getEnvironmentList("e");

		// then
		assertThat(envPage).isNotNull();

		assertThat(envPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(310)
	public void get_environment_base_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		List<EmsEnvironmentBase> environmentBaseList =
				environmentService.getEnvironmentBaseList();

		// then
		assertThat(environmentBaseList).isNotNull();

		log.debug("environmentBaseList {}", StrUtils.jsonDump(environmentBaseList));

		assertThat(environmentBaseList.size()).isGreaterThan(0);
	}

	@Test
	@Order(400)
	public void update_environment_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String childEnvName = "env_001_001";

		// when
		EmsEnvironment childEnv = environmentService.getEnvironmentByName(childEnvName);

		// then
		assertThat(childEnv).isNotNull();

		log.debug("childEnv {}", StrUtils.jsonDump(childEnv));

		assertThat(childEnv.getName()).isEqualTo(childEnvName);

		// given
		Long envId = childEnv.getId();
		String newName = childEnvName + "_updated";
		EmsEnvironmentUpdateParam updateParam = new EmsEnvironmentUpdateParam();
		updateParam.setName(newName);

		// when
		int updated = environmentService.updateEnvironment(envId, updateParam);

		// then
		assertThat(updated).isEqualTo(1);

		// when
		childEnv = environmentService.getEnvironmentById(envId);

		// then
		assertThat(childEnv).isNotNull();

		log.debug("childEnv {}", StrUtils.jsonDump(childEnv));

		assertThat(childEnv.getName()).isEqualTo(newName);
	}

	@Test
	@Order(1000)
	public void add_environment_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSet001 = serviceTestHelper.getUserByName("set001");

		// given
		String envName = "env_001";

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		EmsEnvironmentUserRelationFull relationFull = new EmsEnvironmentUserRelationFull();
		relationFull.setEnvironmentId(environment.getId());
		relationFull.setUserId(userSet001.getId());
		relationFull.setUsername(userSet001.getUsername());
		relationFull.setIsOwner(false);
		relationFull.setIsEditor(true);

		// when
		int cnt = environmentService.addEnvironmentUserRelation(relationFull);

		// then
		assertThat(cnt).isEqualTo(1);

		// when
		CommonPage<EmsEnvironmentUserRelationFull> relationPage =
				environmentService.getEnvironmentUserRelationListByEnvId(
						environment.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);
	}

	@Test
	@Order(1010)
	public void del_environment_user_relation_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		UmsUser userSet001 = serviceTestHelper.getUserByName("set001");

		// given
		String envName = "env_001";

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		EmsEnvironmentUserRelationFull relationFull = new EmsEnvironmentUserRelationFull();
		relationFull.setEnvironmentId(environment.getId());
		relationFull.setUserId(userSet001.getId());
		relationFull.setUsername(userSet001.getUsername());
		relationFull.setIsOwner(false);
		relationFull.setIsEditor(true);

		// when
		int cnt = environmentService.addEnvironmentUserRelation(relationFull);

		// then
		assertThat(cnt).isEqualTo(1);

		// when
		CommonPage<EmsEnvironmentUserRelationFull> relationPage =
				environmentService.getEnvironmentUserRelationListByEnvId(
						environment.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(1);

		relationFull = relationPage.getList().stream()
				.filter(relation -> {
					return relation.getUsername().equals(userSet001.getUsername());
				})
				.findAny()
				.orElse(null);

		assertThat(relationFull).isNotNull();

		// when
		cnt = environmentService.delEnvironmentUserRelationById(relationFull.getId());

		// then
		assertThat(cnt).isEqualTo(1);
	}

	@Test
	@Order(1020)
	public void get_environment_user_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String envName = "env_001";

		EmsEnvironment environment = serviceTestHelper.getEnvironmentByName(envName);

		// when
		CommonPage<EmsEnvironmentUserRelationFull> relationPage =
				environmentService.getEnvironmentUserRelationListByEnvId(
						environment.getId(), 20, 1);

		// then
		assertThat(relationPage).isNotNull();

		log.debug("relationPage {}", StrUtils.jsonDump(relationPage));

		assertThat(relationPage.getList().size()).isGreaterThan(0);
	}

	private CommonPage<EmsEnvironmentFull> getEnvironmentList(String keyword) {
		// given
		EmsEnvironmentQueryParam queryParam = new EmsEnvironmentQueryParam();
		queryParam.setKeyword(keyword);
		queryParam.setSortBy("name");

		// when
		CommonPage<EmsEnvironmentFull> envPage = environmentService.getEnvironmentList(
				queryParam, 20, 1);

		// then
		assertThat(envPage).isNotNull();

		log.debug("envPage {}", StrUtils.jsonDump(envPage));

		return envPage;
	}
}
