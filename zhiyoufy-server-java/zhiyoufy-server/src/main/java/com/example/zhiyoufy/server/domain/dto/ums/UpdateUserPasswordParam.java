package com.example.zhiyoufy.server.domain.dto.ums;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.example.zhiyoufy.common.util.jsonviews.PrivateView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改用户名密码参数
 */
@Getter
@Setter
public class UpdateUserPasswordParam {
	@NotEmpty
	private String username;
	@NotEmpty
	@JsonView(PrivateView.class)
	private String oldPassword;
	@NotEmpty
	@Size(min=6)
	@JsonView(PrivateView.class)
	private String newPassword;
}
