package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRolePermissionRelation;
import org.apache.ibatis.annotations.Param;

public interface UmsRolePermissionRelationDao {
	int insertRolePermissionRelationList(
			@Param("list") List<UmsRolePermissionRelation> relationList);

	List<UmsPermission> getPermissionListByRoleId(@Param("roleId") Long roleId);
}
