package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ums.*;

import org.springframework.security.core.AuthenticationException;

public interface UmsUserService {
	//region Auth
	/**
	 * 检查密码是否合法
	 */
	void checkPasswordValidity(String password);

	/**
	 * 登录功能
	 */
	LoginResponseData formLogin(FormLoginParam param);

	/**
	 * 根据用户名获取用户
	 */
	UmsUser getUserByUsername(String username);

	/**
	 * 获取当前登录用户详情
	 */
	UmsUserDetails getUserDetails();

	/**
	 * 获取当前登录用户信息
	 */
	UserInfoData getUserInfo();

	/**
	 * 获取用户信息
	 */
	UmsUserDetails loadUserDetailsByToken(String token) throws AuthenticationException;

	void logout();

	/**
	 * 请求用户标识码
	 *
	 * 获得标识码的人才能注册，以证明标识归属，比如确实能访问指定邮箱
	 */
	void requestIdentificationCode(RequestIdentificationCodeParam param);

	/**
	 * 注册功能
	 */
	UmsUserDTO register(UmsUserParam umsUserParam);
	//endregion

	//region PermissionCRUD
	/**
	 * 管理员直接添加Permission
	 */
	UmsPermission addPermission(UmsPermissionParam permissionParam);

	Integer delPermissionById(Long permissionId);

	CommonPage<UmsPermission> getPermissionList(UmsPermissionQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	int updatePermission(Long id, UmsPermissionParam permissionParam);
	//endregion

	//region RoleCRUD
	/**
	 * 管理员直接添加Role
	 */
	UmsRole addRole(UmsRoleParam roleParam);

	Integer delRoleById(Long roleId);

	CommonPage<UmsRole> getRoleList(UmsRoleQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	List<UmsPermission> getRolePermissionList(Long roleId);

	int updateRole(Long id, UmsRoleParam roleParam);

	int updateRolePermission(Long userId, UmsRolePermissionUpdateData updateData);
	//endregion

	//region UserCRUD
	/**
	 * 管理员直接添加用户
	 */
	UmsUserDTO addUser(UmsUserParam umsUserParam);

	/**
	 * 删除指定用户
	 */
	Integer delUserById(Long userId);

	UmsUser getUserById(Long userId);

	CommonPage<UmsUserBase> getUserBaseList(UmsUserQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	/**
	 * 分页查询用户
	 */
	CommonPage<UmsUserDTO> getUserList(UmsUserQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	/**
	 * 获取用户对应角色
	 */
	List<UmsRole> getUserRoleList(Long userId);

	/**
	 * 修改指定用户信息
	 */
	int updateUser(Long id, UmsUserUpdateParam updateParam);

	/**
	 * 修改密码
	 */
	int updateUserPassword(UpdateUserPasswordParam updatePasswordParam);

	/**
	 * 修改用户角色关系
	 */
	int updateUserRole(Long userId, UmsUserRoleUpdateData updateData);
	//endregion

	/**
	 * 刷新token的功能
	 * @param oldToken 旧的token
	 */
	String refreshToken(String oldToken);

	/**
	 * 仅做身份验证，不分配token
	 */
	VirtualLoginResponseData virtualLogin(FormLoginParam param);
}
