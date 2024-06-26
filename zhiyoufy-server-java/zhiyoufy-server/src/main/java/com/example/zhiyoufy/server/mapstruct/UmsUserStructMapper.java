package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.domain.dto.ums.UmsPermissionParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsRoleParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UmsUserStructMapper {
	UmsUserStructMapper INSTANCE = Mappers.getMapper( UmsUserStructMapper.class );

	UmsPermission umsPermissionParamToUmsPermission(UmsPermissionParam umsPermissionParam);
	UmsRole umsRoleParamToUmsRole(UmsRoleParam umsRoleParam);

	UmsUser umsUserParamToUmsUser(UmsUserParam umsUserParam);

	UmsUser umsUserUpdateParamToUmsUser(UmsUserUpdateParam updateParam);

	UmsUserDTO umsUserToUmsUserDTO(UmsUser umsUser);
}
