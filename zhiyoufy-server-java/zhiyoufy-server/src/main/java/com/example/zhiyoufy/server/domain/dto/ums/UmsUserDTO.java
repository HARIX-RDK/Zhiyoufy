package com.example.zhiyoufy.server.domain.dto.ums;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UmsUserDTO {
	private Long id;

	private String username;

	private String email;

	private String note;

	private Date createTime;

	private Date loginTime;

	private Boolean enabled;

	private Boolean sysAdmin;
	private Boolean admin;
}
