package com.example.zhiyoufy.server.security;

import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UmsAuthenticationToken extends AbstractAuthenticationToken {
	private UmsUserDetails userDetails;
	private String token;

	public UmsAuthenticationToken(UmsUserDetails userDetails, String token) {
		super(userDetails.getAuthorities());
		this.userDetails = userDetails;
		this.token = token;
		super.setAuthenticated(true);
	}

	public UmsUserDetails getUserDetails() {
		return userDetails;
	}

	public String getToken() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userDetails;
	}
}
