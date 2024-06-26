package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.mbg.model.UmsUserRoleRelation;
import org.apache.ibatis.annotations.Param;

/**
 * 用户与角色关系管理自定义Dao
 */
public interface UmsUserRoleRelationDao {
	/**
	 * 批量插入用户角色关系
	 */
	int insertUserRoleRelationList(@Param("list") List<UmsUserRoleRelation> userRoleRelationList);

	/**
	 * 获取用户所有角色
	 */
	List<UmsRole> getRoleListByUserIdForSecurity(@Param("userId") Long userId);

	/**
	 * 获取用户所有角色
	 */
	List<UmsRole> getRoleListByUserId(@Param("userId") Long userId);

	/**
	 * 获取用户所有权限
	 */
	List<UmsPermission> getPermissionListByUserId(@Param("userId") Long userId);

	/**
	 * 获取权限相关用户ID列表
	 */
	List<Long> getUserIdListByPermissionId(@Param("permissionId") Long permissionId);
}
