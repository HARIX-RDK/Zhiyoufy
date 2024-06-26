package com.example.zhiyoufy.server.domain.dto.ums;

import java.util.List;

import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UmsRolePermissionUpdateData {
	private List<UmsPermission> addPermissions;
	private List<UmsPermission> delPermissions;
}
