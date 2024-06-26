package com.example.zhiyoufy.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.common.util.RequestUtil;
import com.example.zhiyoufy.mbg.mapper.UmsPermissionMapper;
import com.example.zhiyoufy.mbg.mapper.UmsRoleMapper;
import com.example.zhiyoufy.mbg.mapper.UmsRolePermissionRelationMapper;
import com.example.zhiyoufy.mbg.mapper.UmsTokenMapper;
import com.example.zhiyoufy.mbg.mapper.UmsUserLoginLogMapper;
import com.example.zhiyoufy.mbg.mapper.UmsUserMapper;
import com.example.zhiyoufy.mbg.mapper.UmsUserRoleRelationMapper;
import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsPermissionExample;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.mbg.model.UmsRoleExample;
import com.example.zhiyoufy.mbg.model.UmsRolePermissionRelation;
import com.example.zhiyoufy.mbg.model.UmsRolePermissionRelationExample;
import com.example.zhiyoufy.mbg.model.UmsToken;
import com.example.zhiyoufy.mbg.model.UmsTokenExample;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.mbg.model.UmsUserExample;
import com.example.zhiyoufy.mbg.model.UmsUserLoginLog;
import com.example.zhiyoufy.mbg.model.UmsUserRoleRelation;
import com.example.zhiyoufy.mbg.model.UmsUserRoleRelationExample;
import com.example.zhiyoufy.server.config.ZhiyoufyServerProperties;
import com.example.zhiyoufy.server.dao.UmsPermissionDao;
import com.example.zhiyoufy.server.dao.UmsRoleDao;
import com.example.zhiyoufy.server.dao.UmsRolePermissionRelationDao;
import com.example.zhiyoufy.server.dao.UmsUserDao;
import com.example.zhiyoufy.server.dao.UmsUserRoleRelationDao;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDaoQueryParam;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ums.*;
import com.example.zhiyoufy.server.mapstruct.UmsUserStructMapper;
import com.example.zhiyoufy.server.security.UmsAuthenticationToken;
import com.example.zhiyoufy.server.service.EmailService;
import com.example.zhiyoufy.server.service.UmsIdentificationCodeService;
import com.example.zhiyoufy.server.service.UmsUserCacheService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户管理Service实现类
 */
@Service
@Getter
@Setter
@Slf4j
public class UmsUserServiceImpl implements UmsUserService {
	//region properties
	@Autowired
	private ZhiyoufyServerProperties zhiyoufyServerProperties;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UmsPermissionMapper permissionMapper;
	@Autowired
	private UmsRoleMapper roleMapper;
	@Autowired
	private UmsRolePermissionRelationMapper rolePermissionRelationMapper;
	@Autowired
	private UmsTokenMapper tokenMapper;
	@Autowired
	private UmsUserLoginLogMapper userLoginLogMapper;
	@Autowired
	private UmsUserMapper userMapper;
	@Autowired
	private UmsUserRoleRelationMapper userRoleRelationMapper;

	@Autowired
	private UmsPermissionDao permissionDao;
	@Autowired
	private UmsRoleDao roleDao;
	@Autowired
	private UmsRolePermissionRelationDao rolePermissionRelationDao;
	@Autowired
	private UmsUserDao userDao;
	@Autowired
	private UmsUserRoleRelationDao userRoleRelationDao;

	@Autowired
	private UmsUserCacheService userCacheService;
	@Autowired
	private UmsIdentificationCodeService umsIdentificationCodeService;
	@Autowired
	private EmailService emailService;

	private final long minCheckInterval = 600L * 1000;
	private long lastCheckExpireTime = -1;

	private Supplier<Long> timeSupplier;

	private final String emailIdCodePrefix = "email: ";
	private final String timeConsumingEncodedPassword =
			"$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O";
	//endregion

	//region Auth
	@Override
	public void checkPasswordValidity(String password) {
		boolean valid = true;
		boolean hasUpperLetter = false;
		boolean hasLowerLetter = false;
		boolean hasDigit = false;
		boolean hasPunctuation = false;

		if (password == null) {
			valid = false;
		}

		if (valid && password.length() < 6) {
			valid = false;
		}

		if (valid) {
			for (var aChar : password.toCharArray()) {
				if (Character.isUpperCase(aChar)) {
					hasUpperLetter = true;
				} else if (Character.isLowerCase(aChar)) {
					hasLowerLetter = true;
				} else if (Character.isDigit(aChar)) {
					hasDigit = true;
				} else if (isPunctuation(aChar)) {
					hasPunctuation = true;
				}
			}

			if (!hasUpperLetter || !hasLowerLetter || !hasDigit || !hasPunctuation) {
				valid = false;
			}
		}

		if (!valid) {
			Asserts.fail(CommonErrorCode.RES_NON_CONFORMING_PASSWORD);
		}
	}

	@Override
	@Transactional
	public LoginResponseData formLogin(FormLoginParam param) {
		Assert.notNull(param, "param is null");
		Assert.hasText(param.getUsername(), "username is empty");
		Assert.hasText(param.getPassword(), "password is empty");

		String username = param.getUsername();
		String password = param.getPassword();

		String token = null;

		try {
			// 验证用户信息
			UmsUser user = getUserByUsername(username);

			if (user == null) {
				passwordEncoder.matches(RandomUtils.generateShortHexId(),
						timeConsumingEncodedPassword);
				Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
			}

			if (!passwordEncoder.matches(password, user.getPassword()) ||
					!user.getEnabled()) {
				Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
			}

			// 加载详情
			UmsUserDetails userDetails = loadUserDetailsByUserId(user.getId());

			userCacheService.setUser(user);
			userCacheService.setUserDetails(userDetails);

			UmsAuthenticationToken authentication = new UmsAuthenticationToken(userDetails, "");
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// token相关, 去除过期的，生成新的并放入缓存
			removeExpiredTokenIfNeeded();

			long curTime = getCurrentTime();
			Date expireAt = new Date(curTime +
					zhiyoufyServerProperties.getTokenExpireAfterSecs() * 1000);

			token = RandomUtils.generateHexId();

			UmsToken umsToken = new UmsToken();
			umsToken.setUserId(user.getId());
			umsToken.setValue(token);
			umsToken.setCreateTime(new Date(curTime));
			umsToken.setExpireTime(expireAt);

			tokenMapper.insert(umsToken);
			insertLoginLog(user);

			userCacheService.setToken(umsToken);

			// 用户侧可根据expireAt适时刷新
			LoginResponseData responseData = LoginResponseData.builder()
					.token(token)
					.expireAt(expireAt)
					.build();

			return responseData;
		} catch (Exception ex) {
			log.warn("login met exception: {}", ex.getMessage());

			throw ex;
		}
	}

	@Override
	public UmsUser getUserByUsername(String username) {
		UmsUser user = getUserByUsernameFromDb(username);

		if (user != null) {
			userCacheService.setUser(user);
		}

		return user;
	}

	@Override
	public UmsUserDetails getUserDetails() {
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof UmsAuthenticationToken)) {
			Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
		}

		UmsAuthenticationToken umsAuthenticationToken = (UmsAuthenticationToken)authentication;
		UmsUserDetails userDetails = umsAuthenticationToken.getUserDetails();

		return userDetails;
	}

	@Override
	public UserInfoData getUserInfo() {
		UmsUserDetails userDetails = getUserDetails();

		UserInfoData userInfoData = new UserInfoData();
		userInfoData.setUsername(userDetails.getUsername());
		userInfoData.setRoles(userDetails.getRoleNameList());

		return userInfoData;
	}

	@Override
	public UmsUserDetails loadUserDetailsByToken(String token) throws AuthenticationException {
		long curTime = getCurrentTime();

		UmsToken umsToken = userCacheService.getTokenByValue(token);

		if (umsToken == null) {
			boolean isInvalid = userCacheService.isTokenInvalidForSure(token);

			if (isInvalid) {
				throw new BadCredentialsException("invalid token: value");
			}

			umsToken = getTokenByValueFromDb(token);

			if (umsToken == null) {
				userCacheService.setTokenInvalid(token);

				throw new BadCredentialsException("invalid token: value");
			}

			userCacheService.setToken(umsToken);
		}

		if (umsToken.getExpireTime().getTime() < curTime ||
				umsToken.getCreateTime().getTime() > curTime) {
			throw new BadCredentialsException("invalid token: time");
		}

		UmsUserDetails userDetails = loadUserDetailsByUserId(umsToken.getUserId());

		if (userDetails == null) {
			throw new BadCredentialsException("invalid token: userDetails");
		}

		return userDetails;
	}

	@Override
	public void logout() {
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof UmsAuthenticationToken)) {
			Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
		}

		UmsAuthenticationToken umsAuthenticationToken = (UmsAuthenticationToken)authentication;
		String token = umsAuthenticationToken.getToken();

		userCacheService.removeToken(token);

		UmsTokenExample example = new UmsTokenExample();
		example.createCriteria().andValueEqualTo(token);

		int cnt = tokenMapper.deleteByExample(example);

		if (cnt > 0) {
			log.debug("logout removed {} tokens", cnt);
		}
	}

	@Override
	public void requestIdentificationCode(RequestIdentificationCodeParam param) {
		Assert.notNull(param, "param is null");
		Assert.notNull(param.getKind(), "param.kind is null");

		if (param.getKind().equals("email")) {
			requestIdentificationCodeToEmail(param);
		} else {
			throw new IllegalArgumentException("unsupported kind" + param.getKind());
		}
	}

	@Override
	public UmsUserDTO register(UmsUserParam umsUserParam) {
		Assert.notNull(umsUserParam, "umsUserParam is null");
		Assert.hasText(umsUserParam.getUsername(), "username is empty");
		Assert.hasText(umsUserParam.getEmail(), "email is empty");
		Assert.hasText(umsUserParam.getIdCode(), "idCode is empty");

		UmsUser umsUser = getUserByUsername(umsUserParam.getUsername());

		if (umsUser != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		umsUser = getUserByEmailFromDb(umsUserParam.getEmail());

		if (umsUser != null) {
			Asserts.fail(CommonErrorCode.RES_EMAIL_IN_USE);
		}

		String idKey = emailIdCodePrefix + umsUserParam.getEmail();
		String localCode = umsIdentificationCodeService.getIdentificationCode(idKey);

		if (localCode == null || !localCode.equals(umsUserParam.getIdCode())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		checkPasswordValidity(umsUserParam.getPassword());

		umsUser = UmsUserStructMapper.INSTANCE.umsUserParamToUmsUser(umsUserParam);
		umsUser.setCreateTime(new Date());
		umsUser.setSysAdmin(false);
		umsUser.setAdmin(false);
		umsUser.setEnabled(true);

		//将密码进行加密操作
		String encodedPassword = passwordEncoder.encode(umsUser.getPassword());
		umsUser.setPassword(encodedPassword);
		userMapper.insert(umsUser);

		UmsUserDTO umsUserDTO = UmsUserStructMapper.INSTANCE.umsUserToUmsUserDTO(umsUser);

		return umsUserDTO;
	}
	//endregion

	//region PermissionCRUD
	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public UmsPermission addPermission(UmsPermissionParam permissionParam) {
		Assert.notNull(permissionParam, "permissionParam is null");
		Assert.hasText(permissionParam.getName(), "name is empty");

		UmsPermission umsPermission = UmsUserStructMapper.INSTANCE
				.umsPermissionParamToUmsPermission(permissionParam);
		umsPermission.setBuiltin(false);

		permissionMapper.insert(umsPermission);

		return umsPermission;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public Integer delPermissionById(Long permissionId) {
		UmsPermission umsPermission = permissionMapper.selectByPrimaryKey(permissionId);

		if (umsPermission.getBuiltin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		return permissionMapper.deleteByPrimaryKey(permissionId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/admin')")
	public CommonPage<UmsPermission> getPermissionList(UmsPermissionQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);
		UmsPermissionExample example = new UmsPermissionExample();
		UmsPermissionExample.Criteria criteria = example.createCriteria();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			criteria.andNameLike("%" + queryParam.getKeyword() + "%");
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			example.setOrderByClause(orderBy);
		}

		List<UmsPermission> umsPermissionList = permissionMapper.selectByExample(example);

		return CommonPage.restPage(umsPermissionList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public int updatePermission(Long id, UmsPermissionParam permissionParam) {
		Assert.notNull(permissionParam, "permissionParam is null");

		UmsPermission umsPermission = permissionMapper.selectByPrimaryKey(id);

		if (umsPermission.getBuiltin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		umsPermission = UmsUserStructMapper.INSTANCE
				.umsPermissionParamToUmsPermission(permissionParam);
		umsPermission.setId(id);

		return permissionMapper.updateByPrimaryKeySelective(umsPermission);
	}
	//endregion

	//region RoleCRUD
	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public UmsRole addRole(UmsRoleParam roleParam) {
		Assert.notNull(roleParam, "roleParam is null");
		Assert.hasText(roleParam.getName(), "name is empty");

		UmsRole umsRole = UmsUserStructMapper.INSTANCE
				.umsRoleParamToUmsRole(roleParam);
		umsRole.setBuiltin(false);

		roleMapper.insert(umsRole);

		return umsRole;
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public Integer delRoleById(Long roleId) {
		UmsRole umsRole = roleMapper.selectByPrimaryKey(roleId);

		if (umsRole.getBuiltin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		{
			UmsUserRoleRelationExample example = new UmsUserRoleRelationExample();
			UmsUserRoleRelationExample.Criteria criteria = example.createCriteria();

			criteria.andRoleIdEqualTo(roleId);

			userRoleRelationMapper.deleteByExample(example);
		}

		{
			UmsRolePermissionRelationExample example = new UmsRolePermissionRelationExample();
			UmsRolePermissionRelationExample.Criteria criteria = example.createCriteria();

			criteria.andRoleIdEqualTo(roleId);

			rolePermissionRelationMapper.deleteByExample(example);
		}

		return roleMapper.deleteByPrimaryKey(roleId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/admin')")
	public CommonPage<UmsRole> getRoleList(UmsRoleQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);
		UmsRoleExample example = new UmsRoleExample();
		UmsRoleExample.Criteria criteria = example.createCriteria();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			criteria.andNameLike("%" + queryParam.getKeyword() + "%");
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			example.setOrderByClause(orderBy);
		}

		List<UmsRole> umsRoleList = roleMapper.selectByExample(example);

		return CommonPage.restPage(umsRoleList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public List<UmsPermission> getRolePermissionList(Long roleId) {
		return rolePermissionRelationDao.getPermissionListByRoleId(roleId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public int updateRole(Long id, UmsRoleParam roleParam) {
		Assert.notNull(roleParam, "roleParam is null");

		UmsRole umsRole = roleMapper.selectByPrimaryKey(id);

		if (umsRole.getBuiltin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		umsRole = UmsUserStructMapper.INSTANCE.umsRoleParamToUmsRole(roleParam);
		umsRole.setId(id);

		return roleMapper.updateByPrimaryKeySelective(umsRole);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public int updateRolePermission(Long roleId, UmsRolePermissionUpdateData updateData) {
		Assert.notNull(updateData, "updateData is null");

		UmsRole umsRole = roleMapper.selectByPrimaryKey(roleId);

		if (umsRole.getBuiltin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		int updateCnt = 0;

		Map<Long, UmsPermission> curPermissionMap = getPermissionMap(roleId);
		Map<Long, UmsPermission> allPermissionMap = getAllPermissionMap();

		List<UmsRolePermissionRelation> relationList = new ArrayList<>();
		List<Long> permissionIdList = new ArrayList<>();

		if (updateData.getAddPermissions() != null && updateData.getAddPermissions().size() > 0) {
			for (var permission : updateData.getAddPermissions()) {
				Long permissionId = permission.getId();

				if (permissionId == null
						|| curPermissionMap.containsKey(permissionId)
						|| !allPermissionMap.containsKey(permissionId)) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				if (permission.getName() == null ||
						!permission.getName().equals(allPermissionMap.get(permissionId).getName())) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				UmsRolePermissionRelation relation = new UmsRolePermissionRelation();
				relation.setPermissionId(permissionId);
				relation.setRoleId(roleId);

				relationList.add(relation);
			}
		}

		if (updateData.getDelPermissions() != null && updateData.getDelPermissions().size() > 0) {
			for (var permission : updateData.getDelPermissions()) {
				Long permissionId = permission.getId();

				if (permissionId == null
						|| !curPermissionMap.containsKey(permissionId)) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				if (permission.getName() == null ||
						!permission.getName().equals(allPermissionMap.get(permissionId).getName())) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				permissionIdList.add(permissionId);
			}
		}

		if (relationList.size() > 0) {
			int added = rolePermissionRelationDao.insertRolePermissionRelationList(relationList);
			updateCnt += added;
		}

		if (permissionIdList.size() > 0) {
			UmsRolePermissionRelationExample example = new UmsRolePermissionRelationExample();
			UmsRolePermissionRelationExample.Criteria criteria = example.createCriteria();

			criteria.andRoleIdEqualTo(roleId).andPermissionIdIn(permissionIdList);

			int deleted = rolePermissionRelationMapper.deleteByExample(example);
			updateCnt += deleted;
		}

		return updateCnt;
	}
	//endregion

	//region UserCRUD
	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public UmsUserDTO addUser(UmsUserParam umsUserParam) {
		Assert.notNull(umsUserParam, "umsUserParam is null");
		Assert.hasText(umsUserParam.getUsername(), "username is empty");
		Assert.hasText(umsUserParam.getPassword(), "password is empty");
		Assert.hasText(umsUserParam.getEmail(), "email is empty");

		UmsUser umsUser = getUserByUsername(umsUserParam.getUsername());

		if (umsUser != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		umsUser = getUserByEmailFromDb(umsUserParam.getEmail());

		if (umsUser != null) {
			Asserts.fail(CommonErrorCode.RES_EMAIL_IN_USE);
		}

		checkPasswordValidity(umsUserParam.getPassword());

		if (umsUserParam.getEnabled() == null) {
			umsUserParam.setEnabled(true);
		}

		umsUser = UmsUserStructMapper.INSTANCE.umsUserParamToUmsUser(umsUserParam);
		umsUser.setCreateTime(new Date());

		//将密码进行加密操作
		String encodedPassword = passwordEncoder.encode(umsUser.getPassword());
		umsUser.setPassword(encodedPassword);

		userMapper.insertSelective(umsUser);

		UmsUserDTO umsUserDTO = UmsUserStructMapper.INSTANCE.umsUserToUmsUserDTO(umsUser);

		return umsUserDTO;
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public Integer delUserById(Long userId) {
		{
			UmsUserRoleRelationExample example = new UmsUserRoleRelationExample();
			UmsUserRoleRelationExample.Criteria criteria = example.createCriteria();

			criteria.andUserIdEqualTo(userId);

			userRoleRelationMapper.deleteByExample(example);
		}

		return userMapper.deleteByPrimaryKey(userId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/admin')")
	public UmsUser getUserById(Long userId) {
		UmsUser user = userCacheService.getUserById(userId);

		if (user != null) {
			return user;
		}

		user = userMapper.selectByPrimaryKey(userId);

		if (user != null) {
			userCacheService.setUser(user);
		}

		return user;
	}

	@Override
	public CommonPage<UmsUserBase> getUserBaseList(UmsUserQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDaoQueryParam daoQueryParam = convertQueryParam(queryParam);

		PageHelper.startPage(pageNum, pageSize);

		List<UmsUserBase> userBaseList =
				userDao.getUserBaseList(daoQueryParam);

		return CommonPage.restPage(userBaseList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/admin')")
	public CommonPage<UmsUserDTO> getUserList(UmsUserQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails curUserDetails = getUserDetails();
		UmsUserDaoQueryParam daoQueryParam = convertQueryParam(queryParam);

		UmsUserExample example = new UmsUserExample();
		UmsUserExample.Criteria criteria = example.createCriteria();

		if (!curUserDetails.isSysAdmin()) {
			daoQueryParam.setSysAdmin(false);
		} else {
			if (queryParam.getSysAdmin() != null) {
				daoQueryParam.setSysAdmin(queryParam.getSysAdmin());
			}
		}

		if (queryParam.getAdmin() != null) {
			daoQueryParam.setAdmin(queryParam.getAdmin());
		}

		PageHelper.startPage(pageNum, pageSize);

		List<UmsUserDTO> umsUserList = userDao.getUserList(daoQueryParam);

		return CommonPage.restPage(umsUserList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/admin')")
	public List<UmsRole> getUserRoleList(Long userId) {
		return userRoleRelationDao.getRoleListByUserId(userId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/sysAdmin')")
	public int updateUser(Long id, UmsUserUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		UmsUser targetUser = getUserById(id);

		if (targetUser == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		if (updateParam.getPassword() != null) {
			checkPasswordValidity(updateParam.getPassword());

			String encodedPassword = passwordEncoder.encode(updateParam.getPassword());

			updateParam.setPassword(encodedPassword);
		}

		UmsUser umsUser = UmsUserStructMapper.INSTANCE.umsUserUpdateParamToUmsUser(updateParam);
		umsUser.setId(id);

		userCacheService.removeUser(umsUser);

		return userMapper.updateByPrimaryKeySelective(umsUser);
	}

	@Override
	public int updateUserPassword(UpdateUserPasswordParam updatePasswordParam) {
		UmsUserDetails curUserDetails = getUserDetails();
		UmsUser umsUser = curUserDetails.getUmsUser();

		if (!curUserDetails.getUsername().equals(updatePasswordParam.getUsername())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		if (!passwordEncoder.matches(updatePasswordParam.getOldPassword(), umsUser.getPassword())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		checkPasswordValidity(updatePasswordParam.getNewPassword());

		String encodedPassword = passwordEncoder.encode(updatePasswordParam.getNewPassword());

		UmsUser updateUser = new UmsUser();
		updateUser.setId(umsUser.getId());
		updateUser.setPassword(encodedPassword);

		return userMapper.updateByPrimaryKeySelective(updateUser);
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('roles/admin')")
	public int updateUserRole(Long userId, UmsUserRoleUpdateData updateData) {
		Assert.notNull(updateData, "updateData is null");

		UmsUserDetails curUserDetails = getUserDetails();
		UmsUser targetUser = getUserById(userId);

		if (targetUser == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		int updateCnt = 0;

		Map<Long, UmsRole> curRoleMap = getRoleMap(userId);
		Map<Long, UmsRole> allRoleMap = getAllRoleMap();

		if (targetUser.getAdmin() && !curUserDetails.isSysAdmin() && !curUserDetails.getUserId()
				.equals(userId)) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		List<UmsUserRoleRelation> userRoleRelationList = new ArrayList<>();
		List<Long> roleIdList = new ArrayList<>();

		if (updateData.getAddRoles() != null && updateData.getAddRoles().size() > 0) {
			for (var role : updateData.getAddRoles()) {
				Long roleId = role.getId();

				if (roleId == null
						|| curRoleMap.containsKey(roleId)
						|| !allRoleMap.containsKey(roleId)) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				if (role.getName() == null ||
						!role.getName().equals(allRoleMap.get(roleId).getName())) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				UmsUserRoleRelation userRoleRelation = new UmsUserRoleRelation();
				userRoleRelation.setUserId(userId);
				userRoleRelation.setRoleId(roleId);

				userRoleRelationList.add(userRoleRelation);
			}
		}

		if (updateData.getDelRoles() != null && updateData.getDelRoles().size() > 0) {
			for (var role : updateData.getDelRoles()) {
				Long roleId = role.getId();

				if (roleId == null
						|| !curRoleMap.containsKey(roleId)) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				if (role.getName() == null ||
						!role.getName().equals(allRoleMap.get(roleId).getName())) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
				}

				roleIdList.add(roleId);
			}
		}

		if (userRoleRelationList.size() > 0) {
			int added = userRoleRelationDao.insertUserRoleRelationList(userRoleRelationList);
			updateCnt += added;
		}

		if (roleIdList.size() > 0) {
			UmsUserRoleRelationExample example = new UmsUserRoleRelationExample();
			UmsUserRoleRelationExample.Criteria criteria = example.createCriteria();

			criteria.andUserIdEqualTo(userId).andRoleIdIn(roleIdList);

			int deleted = userRoleRelationMapper.deleteByExample(example);
			updateCnt += deleted;
		}

		if (updateCnt > 0) {
			userCacheService.removeUserDetailsByUserId(userId);
		}

		return updateCnt;
	}
	//endregion

	@Override
	public String refreshToken(String oldToken) {
		return null;
	}

	//region private functions
	private UmsUserDaoQueryParam convertQueryParam(UmsUserQueryParam queryParam) {
		UmsUserDaoQueryParam daoQueryParam = new UmsUserDaoQueryParam();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			daoQueryParam.setKeyword("%" + queryParam.getKeyword() + "%");
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			daoQueryParam.setOrderByClause(orderBy);
		}

		return daoQueryParam;
	}

	private Map<Long, UmsPermission> getAllPermissionMap() {
		List<UmsPermission> umsPermissionList = permissionDao.getAllPermissionList();

		Map<Long, UmsPermission> umsPermissionMap = new HashMap<>();

		for (var permission : umsPermissionList) {
			umsPermissionMap.put(permission.getId(), permission);
		}

		return umsPermissionMap;
	}

	private Map<Long, UmsRole> getAllRoleMap() {
		List<UmsRole> umsRoleList = roleDao.getAllRoleList();

		Map<Long, UmsRole> umsRoleMap = new HashMap<>();

		for (var role : umsRoleList) {
			umsRoleMap.put(role.getId(), role);
		}

		return umsRoleMap;
	}

	private long getCurrentTime() {
		if (timeSupplier == null) {
			return System.currentTimeMillis();
		} else {
			return timeSupplier.get();
		}
	}

	private Map<Long, UmsPermission> getPermissionMap(long roleId) {
		List<UmsPermission> umsPermissionList = getRolePermissionList(roleId);

		Map<Long, UmsPermission> umsPermissionMap = new HashMap<>();

		for (var permission : umsPermissionList) {
			umsPermissionMap.put(permission.getId(), permission);
		}

		return umsPermissionMap;
	}

	private Map<Long, UmsRole> getRoleMap(long userId) {
		List<UmsRole> umsRoleList = getUserRoleList(userId);

		Map<Long, UmsRole> umsRoleMap = new HashMap<>();

		for (var role : umsRoleList) {
			umsRoleMap.put(role.getId(), role);
		}

		return umsRoleMap;
	}

	private UmsToken getTokenByValueFromDb(String value) {
		UmsTokenExample example = new UmsTokenExample();
		example.createCriteria().andValueEqualTo(value);

		List<UmsToken> tokenList = tokenMapper.selectByExample(example);

		if (tokenList != null && tokenList.size() > 0) {
			UmsToken token = tokenList.get(0);
			return token;
		}

		return null;
	}

	private UmsUser getUserByEmailFromDb(String email) {
		UmsUserExample example = new UmsUserExample();
		example.createCriteria().andEmailEqualTo(email);

		List<UmsUser> userList = userMapper.selectByExample(example);

		if (userList != null && userList.size() > 0) {
			UmsUser user = userList.get(0);
			userCacheService.setUser(user);
			return user;
		}

		return null;
	}

	private UmsUser getUserByUsernameFromDb(String username) {
		UmsUserExample example = new UmsUserExample();
		example.createCriteria().andUsernameEqualTo(username);

		List<UmsUser> userList = userMapper.selectByExample(example);

		if (userList != null && userList.size() > 0) {
			UmsUser user = userList.get(0);
			userCacheService.setUser(user);
			return user;
		}

		return null;
	}

	/**
	 * 添加登录记录
	 */
	private void insertLoginLog(UmsUser user) {
		long curTime = getCurrentTime();
		Date loginTime = new Date(curTime);

		UmsUserLoginLog loginLog = new UmsUserLoginLog();

		loginLog.setUserId(user.getId());
		loginLog.setCreateTime(loginTime);

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			loginLog.setIp(RequestUtil.getRequestIp(request));
		} else {
			loginLog.setIp("");
		}

		userLoginLogMapper.insert(loginLog);

		UmsUser userUpdate = new UmsUser();
		userUpdate.setLoginTime(loginTime);

		UmsUserExample example = new UmsUserExample();
		example.createCriteria().andIdEqualTo(user.getId());

		userMapper.updateByExampleSelective(userUpdate, example);
	}

	private static boolean isPunctuation(char c) {
		return c == ','
				|| c == '.'
				|| c == '!'
				|| c == '?'
				|| c == ':'
				|| c == ';'
				;
	}

	private UmsUserDetails loadUserDetailsByUmsUserFromDb(UmsUser umsUser) {
		List<UmsRole> roleList =
				userRoleRelationDao.getRoleListByUserIdForSecurity(umsUser.getId());
		List<UmsPermission> permissionList =
				userRoleRelationDao.getPermissionListByUserId(umsUser.getId());

		UmsUserDetails userDetails = new UmsUserDetails(umsUser,
				roleList, permissionList);

		return userDetails;
	}

	private UmsUserDetails loadUserDetailsByUserId(Long userId) {
		UmsUserDetails userDetails =
				userCacheService.getUserDetailsByUserId(userId);

		if (userDetails != null) {
			return userDetails;
		}

		UmsUser umsUser = getUserById(userId);

		if (umsUser == null) {
			return null;
		}

		userDetails = loadUserDetailsByUmsUserFromDb(umsUser);

		userCacheService.setUserDetails(userDetails);

		return userDetails;
	}

	private void removeExpiredTokenIfNeeded() {
		long curTime = getCurrentTime();

		if (Math.abs(curTime - lastCheckExpireTime) < minCheckInterval) {
			return;
		}

		lastCheckExpireTime = curTime;

		UmsTokenExample example = new UmsTokenExample();
		example.createCriteria().andExpireTimeLessThan(new Date(curTime));

		int cnt = tokenMapper.deleteByExample(example);

		if (cnt > 0) {
			log.debug("removed {} expired tokens", cnt);
		}
	}

	private void requestIdentificationCodeToEmail(RequestIdentificationCodeParam param) {
		Assert.hasText(param.getEmail(), "email is empty");

		UmsUser user = getUserByEmailFromDb(param.getEmail());

		if (user != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		String idKey = emailIdCodePrefix + param.getEmail();

		try {
			String code = umsIdentificationCodeService.generateIdentificationCode(idKey);

			String htmlBody = String.format("<h2>identification code: %s</h2>", code);

			emailService.sendMail(param.getEmail(), "identification code for zhiyoufy",
					htmlBody, true);
		} catch (Exception ex) {
			umsIdentificationCodeService.removeIdentificationCode(idKey);

			throw ex;
		}
	}
	//endregion

	@Override
	@Transactional
	public VirtualLoginResponseData virtualLogin(FormLoginParam param) {
		Assert.notNull(param, "param is null");
		Assert.hasText(param.getUsername(), "username is empty");
		Assert.hasText(param.getPassword(), "password is empty");

		String username = param.getUsername();
		String password = param.getPassword();

		try {
			// 验证用户信息
			UmsUser user = getUserByUsername(username);

			if (user == null) {
				Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
			}

			if (!passwordEncoder.matches(password, user.getPassword()) ||
					!user.getEnabled()) {
				Asserts.fail(CommonErrorCode.RES_UNAUTHORIZED);
			}

			long curTime = getCurrentTime();
			Date virtualLoginTime = new Date(curTime);

			return VirtualLoginResponseData.builder()
					.id(user.getId())
					.username(user.getUsername())
					.email(user.getEmail())
					.admin(user.getAdmin())
					.enabled(user.getEnabled())
					.sysAdmin(user.getSysAdmin())
					.createTime(user.getCreateTime())
					.virtualLoginTime(virtualLoginTime)
					.note(user.getNote())
					.build();

		} catch (Exception ex) {
			log.warn("virtualLogin met exception: {}", ex.getMessage());
			throw ex;
		}
	}
}
