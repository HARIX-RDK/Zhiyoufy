package com.example.zhiyoufy.server.domain.dto.ums;

import java.util.List;

import com.example.zhiyoufy.mbg.model.UmsRole;
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
public class UmsUserRoleUpdateData {
	private List<UmsRole> addRoles;
	private List<UmsRole> delRoles;
}
