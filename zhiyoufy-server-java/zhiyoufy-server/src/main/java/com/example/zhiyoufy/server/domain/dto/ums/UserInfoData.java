package com.example.zhiyoufy.server.domain.dto.ums;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoData {
	private String username;
	private List<String> roles;
}
