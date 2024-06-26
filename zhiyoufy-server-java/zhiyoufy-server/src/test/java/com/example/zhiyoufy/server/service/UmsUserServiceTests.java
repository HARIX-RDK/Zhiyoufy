package com.example.zhiyoufy.server.service;

import java.util.ArrayList;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.server.config.ServiceTestGroupGeneralConfig;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ums.FormLoginParam;
import com.example.zhiyoufy.server.domain.dto.ums.LoginResponseData;
import com.example.zhiyoufy.server.domain.dto.ums.RequestIdentificationCodeParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsRoleQueryParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserBase;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserQueryParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserRoleUpdateData;
import com.example.zhiyoufy.server.domain.dto.ums.UserInfoData;
import com.example.zhiyoufy.server.service.impl.UmsIdentificationCodeServiceImpl;
import com.example.zhiyoufy.server.service.impl.UmsUserServiceImpl;
import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UmsUserServiceTests {
	//region properties
	@Autowired
	ServiceTestHelper serviceTestHelper;

	@Autowired
	UmsUserService userService;
	@Autowired
	UmsUserServiceImpl umsUserServiceImpl;
	@Autowired
	UmsUserCacheService userCacheService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	ZhiyoufyTestProperties zhiyoufyTestProperties;

	UmsIdentificationCodeServiceImpl umsIdentificationCodeService;
	private long curTime;

	String adminPassword;
	String defaultPassword = "Br3!^^^";
	//endregion

	//region setup & cleanup
	@BeforeEach
	public void setup(TestInfo testInfo) throws Exception {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);

		adminPassword = zhiyoufyTestProperties.getAdminPassword();

		umsIdentificationCodeService = new UmsIdentificationCodeServiceImpl();

		curTime = System.currentTimeMillis();
		umsIdentificationCodeService.setTimeSupplier(this::getCurrentTime);

		umsUserServiceImpl.setUmsIdentificationCodeService(umsIdentificationCodeService);
	}

	@AfterEach
	public void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);

		serviceTestHelper.clearAuthentication();
	}
	//endregion

	//region Auth
	@Test
	@Order(100)
	@Disabled
	public void shouldUseBCryptPasswordEncoder() {
		String password = defaultPassword;

		// when
		String encodedPassword = null;
		for (int i = 0; i < 1; i++) {
			encodedPassword = passwordEncoder.encode(password);
		}

		// then
		assertThat(encodedPassword).isNotNull();
		assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
	}

	@Test
	@Order(200)
	public void checkPasswordValidity_with_password_not_has_uppercase_letter_should_fail() {
		// given
		String password = "r3!^^^";

		// when
		Throwable thrown = catchThrowable(
				() -> userService.checkPasswordValidity(password));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(210)
	public void checkPasswordValidity_with_password_not_has_lowercase_letter_should_fail() {
		// given
		String password = "B3!^^^";

		// when
		Throwable thrown = catchThrowable(
				() -> userService.checkPasswordValidity(password));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(220)
	public void checkPasswordValidity_with_password_not_has_digit_should_fail() {
		// given
		String password = "Br!^^^";

		// when
		Throwable thrown = catchThrowable(
				() -> userService.checkPasswordValidity(password));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(230)
	public void checkPasswordValidity_with_password_not_has_punctuation_should_fail() {
		// given
		String password = "Br3^^^";

		// when
		Throwable thrown = catchThrowable(
				() -> userService.checkPasswordValidity(password));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(300)
	public void requestIdentificationCode_should_be_ok() {
		// given
		RequestIdentificationCodeParam param = RequestIdentificationCodeParam.builder()
				.kind("email")
				.email("new.user@example.com")
				.build();

		// when
		Throwable thrown = catchThrowable(
				() -> userService.requestIdentificationCode(param));

		// then
		assertThat(thrown).isNull();
	}

	@Test
	@Order(310)
	public void requestIdentificationCode_with_exist_email_should_fail() {
		// given
		String email = "admin@example.com";

		RequestIdentificationCodeParam codeParam = RequestIdentificationCodeParam.builder()
				.kind("email")
				.email(email)
				.build();

		// when
		Throwable thrown = catchThrowable(
				() -> userService.requestIdentificationCode(codeParam));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(400)
	public void register_should_be_ok() {
		// given
		String username = "new.user";
		String email = "new.user@example.com";
		String idKey = umsUserServiceImpl.getEmailIdCodePrefix() + email;

		RequestIdentificationCodeParam codeParam = RequestIdentificationCodeParam.builder()
				.kind("email")
				.email(email)
				.build();

		// when
		userService.requestIdentificationCode(codeParam);

		// then
		String idCode = umsIdentificationCodeService.getIdentificationCode(idKey);
		assertThat(idCode).isNotNull();

		// given
		UmsUserParam umsUserParam = UmsUserParam.builder()
				.username(username)
				.password(defaultPassword)
				.email(email)
				.idCode(idCode)
				.build();

		// when
		UmsUserDTO umsUserDTO = userService.register(umsUserParam);

		// then
		assertThat(umsUserDTO).isNotNull();
		assertThat(umsUserDTO.getId()).isNotNull();
		assertThat(umsUserDTO.getUsername()).isEqualTo(umsUserParam.getUsername());
	}

	@Test
	@Order(410)
	public void register_with_exist_name_should_fail() {
		// given
		String username = "admin";
		String email = "new.user@example.com";
		String idKey = umsUserServiceImpl.getEmailIdCodePrefix() + email;

		RequestIdentificationCodeParam codeParam = RequestIdentificationCodeParam.builder()
				.kind("email")
				.email(email)
				.build();

		// when
		userService.requestIdentificationCode(codeParam);

		// then
		String idCode = umsIdentificationCodeService.getIdentificationCode(idKey);
		assertThat(idCode).isNotNull();

		// given
		UmsUserParam umsUserParam = UmsUserParam.builder()
				.username(username)
				.password(defaultPassword)
				.email(email)
				.idCode(idCode)
				.build();

		// when
		Throwable thrown = catchThrowable(
				() -> userService.register(umsUserParam));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(500)
	public void login_should_be_ok() {
		// given
		String username = "admin";
		String password = adminPassword;

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

		// when
		UserInfoData userInfoData = userService.getUserInfo();

		// then
		assertThat(userInfoData).isNotNull();
		assertThat(userInfoData.getUsername()).isEqualTo(username);

		log.debug("userInfoData {}", StrUtils.jsonDump(userInfoData));

		// given
		String token = loginResponseData.getToken();

		// when
		UmsUserDetails userDetails = userService.loadUserDetailsByToken(token);

		// then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUmsUser().getUsername()).isEqualTo(username);

		// given
		userCacheService.clearAll();

		log.debug("simulate load after cache expired");

		// when
		userDetails = userService.loadUserDetailsByToken(token);

		// then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUmsUser().getUsername()).isEqualTo(username);
	}

	@Test
	@Order(510)
	public void login_with_wrong_password_should_fail() {
		// given
		String username = "admin";
		String password = "wrong_password";

		FormLoginParam loginParam = FormLoginParam.builder()
				.username(username)
				.password(password)
				.build();

		// when
		Throwable thrown = catchThrowable(
				() -> userService.formLogin(loginParam));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(520)
	public void login_with_not_exist_username_should_fail() {
		// given
		String username = "not_exist_username";
		String password = "anything";

		FormLoginParam loginParam = FormLoginParam.builder()
				.username(username)
				.password(password)
				.build();

		// when
		Throwable thrown = catchThrowable(
				() -> userService.formLogin(loginParam));

		// then
		assertThat(thrown).isInstanceOf(ErrorCodeException.class);
	}

	@Test
	@Order(600)
	public void loadUserDetailsByToken_with_wrong_token_should_fail() {
		// given
		String token = "wrong_token";

		// when
		Throwable thrown = catchThrowable(
				() -> userService.loadUserDetailsByToken(token));

		// then
		assertThat(thrown).isInstanceOf(AuthenticationException.class);
	}
	//endregion

	//region User
	@Test
	@Order(700)
	public void add_user_should_be_ok() {
		// given
		String username = "new.user";
		UmsUserParam umsUserParam = UmsUserParam.builder()
				.username(username)
				.password(defaultPassword)
				.email(username + "@example.com")
				.build();

		// when
		UmsUserDTO umsUserDTO = userService.addUser(umsUserParam);

		// then
		assertThat(umsUserDTO).isNotNull();

		log.debug("umsUserDTO {}", StrUtils.jsonDump(umsUserDTO));

		assertThat(umsUserDTO.getUsername()).isEqualTo(username);
	}

	@Test
	@Order(710)
	public void del_user_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		CommonPage<UmsUserDTO> userPage = getUserList("set001");

		// then
		assertThat(userPage).isNotNull();
		assertThat(userPage.getTotal()).isEqualTo(1);

		// given
		Long userId = userPage.getList().get(0).getId();

		// when
		List<UmsRole> beforeRoleList = userService.getUserRoleList(userId);
		int deleted = userService.delUserById(userId);
		List<UmsRole> afterRoleList = userService.getUserRoleList(userId);

		// then
		assertThat(deleted).isEqualTo(1);
		assertThat(beforeRoleList.size()).isGreaterThan(0);
		assertThat(afterRoleList.size()).isEqualTo(0);
	}

	@Test
	@Order(720)
	public void get_user_base_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String keyword = "";

		// when
		CommonPage<UmsUserBase> userPage = getUserBaseList(keyword);

		// then
		assertThat(userPage.getList().size()).isGreaterThan(1);

		// given
		keyword = "admin";

		// when
		userPage = getUserBaseList(keyword);

		// then
		assertThat(userPage.getList().size()).isGreaterThan(0);
	}

	@Test
	@Order(721)
	public void get_user_list_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// given
		String keyword = "";

		// when
		CommonPage<UmsUserDTO> userPage = getUserList(keyword);

		// then
		assertThat(userPage.getList().size()).isGreaterThan(1);

		// given
		keyword = "admin";

		// when
		userPage = getUserList(keyword);

		// then
		assertThat(userPage.getList().size()).isEqualTo(1);
	}

	@Test
	@Order(730)
	public void update_user_role_should_be_ok() {
		serviceTestHelper.formLoginAdmin();

		// when
		CommonPage<UmsUserDTO> userPage = getUserList("swe001");

		// then
		assertThat(userPage).isNotNull();
		assertThat(userPage.getTotal()).isEqualTo(1);

		// given
		Long userId = userPage.getList().get(0).getId();
		String addRoleName = "project.viewer";

		// when
		List<UmsRole> beforeRoleList = userService.getUserRoleList(userId);
		CommonPage<UmsRole> rolePage = getRoleList(addRoleName);

		// then
		assertThat(beforeRoleList.size()).isGreaterThan(0);
		assertThat(rolePage.getTotal()).isEqualTo(1);

		// given
		UmsUserRoleUpdateData updateData = new UmsUserRoleUpdateData();
		List<UmsRole> addRoles = new ArrayList<>();
		List<UmsRole> delRoles = new ArrayList<>();

		updateData.setAddRoles(addRoles);
		updateData.setDelRoles(delRoles);

		addRoles.add(rolePage.getList().get(0));
		delRoles.add(beforeRoleList.get(0));

		// when
		int updateCnt = userService.updateUserRole(userId, updateData);

		List<UmsRole> afterRoleList = userService.getUserRoleList(userId);

		// then
		assertThat(updateCnt).isEqualTo(2);
		assertThat(afterRoleList.size()).isEqualTo(beforeRoleList.size());
		assertThat(afterRoleList.stream().anyMatch(role -> {
			return role.getName().equals(addRoleName);
		})).isTrue();
	}
	//endregion

	//region Support
	private CommonPage<UmsRole> getRoleList(String keyword) {
		// given
		UmsRoleQueryParam queryParam = new UmsRoleQueryParam();
		queryParam.setKeyword(keyword);

		// when
		CommonPage<UmsRole> rolePage = userService.getRoleList(queryParam, 20, 1);

		// then
		assertThat(rolePage).isNotNull();

		log.debug("rolePage {}", StrUtils.jsonDump(rolePage));

		return rolePage;
	}

	private CommonPage<UmsUserBase> getUserBaseList(String keyword) {
		// given
		UmsUserQueryParam queryParam = new UmsUserQueryParam();
		queryParam.setKeyword(keyword);

		// when
		CommonPage<UmsUserBase> userPage = userService.getUserBaseList(queryParam, 20, 1);

		// then
		assertThat(userPage).isNotNull();

		log.debug("userPage {}", StrUtils.jsonDump(userPage));

		return userPage;
	}

	private CommonPage<UmsUserDTO> getUserList(String keyword) {
		// given
		UmsUserQueryParam queryParam = new UmsUserQueryParam();
		queryParam.setKeyword(keyword);

		// when
		CommonPage<UmsUserDTO> userPage = userService.getUserList(queryParam, 20, 1);

		// then
		assertThat(userPage).isNotNull();

		log.debug("userPage {}", StrUtils.jsonDump(userPage));

		return userPage;
	}

	public long getCurrentTime() {
		return curTime;
	}
	//endregion
}
