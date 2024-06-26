package com.example.zhiyoufy.server.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.service.UmsUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class UmsBearerTokenFilter extends OncePerRequestFilter {
	@Autowired
	UmsUserService userService;

	public static final String AUTHENTICATION_SCHEME_BEARER = "Bearer ";
	private static final int schemeLen = AUTHENTICATION_SCHEME_BEARER.length();

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = getTokenFromHeader(request);

			if (token == null) {
				filterChain.doFilter(request, response);
				return;
			}

			UmsUserDetails userDetails = userService.loadUserDetailsByToken(token);

			UmsAuthenticationToken authentication =
					new UmsAuthenticationToken(userDetails, token);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);
		} catch (AuthenticationException ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} finally {
			SecurityContextHolder.clearContext();
		}
	}

	private String getTokenFromHeader(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null) {
			return null;
		}

		header = header.trim();

		if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
			throw new BadCredentialsException("Bad authorization header");
		}

		String token = header.substring(schemeLen);

		if (token.length() != 32) {
			throw new BadCredentialsException("Bad authorization header");
		}

		return token;
	}
}
