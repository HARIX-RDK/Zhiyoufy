package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.server.domain.dto.ums.*;
import com.example.zhiyoufy.server.service.UmsUserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_UMS_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_UMS_USER_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_UMS_USER_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_ADD_PERMISSION;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_ADD_ROLE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_ADD_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_DEL_PERMISSION;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_DEL_ROLE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_DEL_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_FORM_LOGIN;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_PERMISSION_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_ROLE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_ROLE_PERMISSION_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_USER_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_USER_INFO;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_USER_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_GET_USER_ROLE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_LOGOUT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_REGISTER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_REQUEST_ID_CODE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_PERMISSION;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_ROLE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_ROLE_PERMISSION_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_USER;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_USER_PASSWORD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_UPDATE_USER_ROLE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_UMS_USER_VIRTUAL_LOGIN;

@RestController
@RequestMapping("/zhiyoufy-api/v1/user")
@Slf4j
public class UmsUserController {
	@Autowired
	UmsUserService userService;

	//region Auth
	@ElkRecordable(type = ZHIYOUFY_UMS_USER_FORM_LOGIN,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/form-login", method = RequestMethod.POST)
	public CommonResult<LoginResponseData> formLogin(
			@Validated @RequestBody FormLoginParam loginParam) {
		LoginResponseData loginResponseData = userService.formLogin(loginParam);

		return CommonResult.success(loginResponseData);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_USER_INFO,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/user-info", method = RequestMethod.GET)
	public CommonResult<UserInfoData> getUserInfo() {
		UserInfoData userInfoData = userService.getUserInfo();

		return CommonResult.success(userInfoData);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_LOGOUT,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public CommonResult logout() {
		userService.logout();

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_REGISTER,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public CommonResult<UmsUserDTO> register(@Validated @RequestBody UmsUserParam umsUserParam) {
		UmsUserDTO umsUserDTO = userService.register(umsUserParam);

		return CommonResult.success(umsUserDTO);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_REQUEST_ID_CODE,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/request-id-code", method = RequestMethod.POST)
	@ResponseBody
	public CommonResult requestIdCode(
			@Validated @RequestBody RequestIdentificationCodeParam param) {
		userService.requestIdentificationCode(param);

		return CommonResult.success();
	}
	//endregion

	//region PermissionCRUD
	@ElkRecordable(type = ZHIYOUFY_UMS_USER_ADD_PERMISSION,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/add-permission", method = RequestMethod.POST)
	public CommonResult<UmsPermission> addPermission(
			@Validated @RequestBody UmsPermissionParam permissionParam) {
		UmsPermission umsPermission = userService.addPermission(permissionParam);

		return CommonResult.success(umsPermission);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_DEL_PERMISSION,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/del-permission/{permissionId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delPermission(@PathVariable Long permissionId) {
		Integer cnt = userService.delPermissionById(permissionId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_PERMISSION_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/permission-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<UmsPermission>> getPermissionList(
			UmsPermissionQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<UmsPermission> permissionPage = userService.getPermissionList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(permissionPage);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_PERMISSION,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-permission/{permissionId}", method = RequestMethod.POST)
	public CommonResult<Integer> updatePermission(@PathVariable Long permissionId,
			@RequestBody UmsPermissionParam permissionParam) {
		Integer cnt = userService.updatePermission(permissionId, permissionParam);

		return CommonResult.success(cnt);
	}
	//endregion

	//region RoleCRUD
	@ElkRecordable(type = ZHIYOUFY_UMS_USER_ADD_ROLE,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/add-role", method = RequestMethod.POST)
	public CommonResult<UmsRole> addRole(
			@Validated @RequestBody UmsRoleParam roleParam) {
		UmsRole umsRole = userService.addRole(roleParam);

		return CommonResult.success(umsRole);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_DEL_ROLE,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/del-role/{roleId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delRole(@PathVariable Long roleId) {
		Integer cnt = userService.delRoleById(roleId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_ROLE_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/role-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<UmsRole>> getRoleList(
			UmsRoleQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<UmsRole> rolePage = userService.getRoleList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(rolePage);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_ROLE_PERMISSION_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/role-permission-list/{roleId}", method = RequestMethod.GET)
	public CommonResult<List<UmsPermission>> getRolePermissionList(@PathVariable Long roleId) {
		List<UmsPermission> permissionList = userService.getRolePermissionList(roleId);

		return CommonResult.success(permissionList);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_ROLE,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-role/{roleId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateRole(@PathVariable Long roleId,
			@RequestBody UmsRoleParam roleParam) {
		Integer cnt = userService.updateRole(roleId, roleParam);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_ROLE_PERMISSION_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-role-permission-list/{roleId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateRolePermissionList(@PathVariable Long roleId,
			@RequestBody UmsRolePermissionUpdateData updateData) {
		Integer cnt = userService.updateRolePermission(roleId, updateData);

		return CommonResult.success(cnt);
	}
	//endregion

	//region UserCRUD
	@ElkRecordable(type = ZHIYOUFY_UMS_USER_ADD_USER,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/add-user", method = RequestMethod.POST)
	public CommonResult<UmsUserDTO> addUser(@Validated @RequestBody UmsUserParam umsUserParam) {
		UmsUserDTO umsUserDTO = userService.addUser(umsUserParam);

		return CommonResult.success(umsUserDTO);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_DEL_USER,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/del-user/{userId}", method = RequestMethod.DELETE)
	public CommonResult<Integer> delUser(@PathVariable Long userId) {
		Integer cnt = userService.delUserById(userId);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_USER_BASE_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/user-base-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<UmsUserBase>> getUserBaseList(
			UmsUserQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<UmsUserBase> userPage = userService.getUserBaseList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(userPage);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_USER_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/user-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<UmsUserDTO>> getUserList(
			UmsUserQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<UmsUserDTO> userPage = userService.getUserList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(userPage);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_GET_USER_ROLE_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_READ})
	@RequestMapping(value = "/user-role-list/{userId}", method = RequestMethod.GET)
	public CommonResult<List<UmsRole>> getUserRoleList(@PathVariable Long userId) {
		List<UmsRole> roleList = userService.getUserRoleList(userId);

		return CommonResult.success(roleList);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_USER,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-user/{userId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateUser(@PathVariable Long userId,
			@RequestBody UmsUserUpdateParam updateParam) {
		Integer cnt = userService.updateUser(userId, updateParam);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_USER_PASSWORD,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-user-password", method = RequestMethod.POST)
	public CommonResult<Integer> updateUserPassword(
			@RequestBody @Validated UpdateUserPasswordParam updatePasswordParam) {
		Integer cnt = userService.updateUserPassword(updatePasswordParam);

		return CommonResult.success(cnt);
	}

	@ElkRecordable(type = ZHIYOUFY_UMS_USER_UPDATE_USER_ROLE_LIST,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/update-user-role-list/{userId}", method = RequestMethod.POST)
	public CommonResult<Integer> updateUserRoleList(@PathVariable Long userId,
			@RequestBody UmsUserRoleUpdateData updateData) {
		Integer cnt = userService.updateUserRole(userId, updateData);

		return CommonResult.success(cnt);
	}
	//endregion

	//region virtual login
	@ElkRecordable(type = ZHIYOUFY_UMS_USER_VIRTUAL_LOGIN,
			tags = {ZHIYOUFY_UMS_USER, ZHIYOUFY_UMS_USER_WRITE})
	@RequestMapping(value = "/virtual-login", method = RequestMethod.POST)
	public CommonResult<VirtualLoginResponseData> virtualLogin(
			@Validated @RequestBody FormLoginParam loginParam) {
		VirtualLoginResponseData loginResponseData = userService.virtualLogin(loginParam);

		return CommonResult.success(loginResponseData);
	}
	//endregion
}
