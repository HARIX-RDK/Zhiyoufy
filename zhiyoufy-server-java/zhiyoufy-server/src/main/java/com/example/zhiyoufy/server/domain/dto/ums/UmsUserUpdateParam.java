package com.example.zhiyoufy.server.domain.dto.ums;

import com.example.zhiyoufy.common.util.jsonviews.PrivateView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
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
public class UmsUserUpdateParam {
	private String username;
	@JsonView(PrivateView.class)
	private String password;
	private String email;
	private String note;
	private Boolean enabled;
	private Boolean sysadmin;
	private Boolean admin;
}
