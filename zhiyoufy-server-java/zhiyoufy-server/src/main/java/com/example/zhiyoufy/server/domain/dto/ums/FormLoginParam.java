package com.example.zhiyoufy.server.domain.dto.ums;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.example.zhiyoufy.common.util.jsonviews.PrivateView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 使用用户名密码登录参数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormLoginParam {
	@NotEmpty
	@Size(min = 3)
	private String username;
	@NotEmpty
	@Size(min = 6)
	@JsonView(PrivateView.class)
	private String password;
}
