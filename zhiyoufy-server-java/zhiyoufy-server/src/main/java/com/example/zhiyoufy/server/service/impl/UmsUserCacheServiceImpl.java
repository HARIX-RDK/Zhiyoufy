package com.example.zhiyoufy.server.service.impl;

import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.mbg.model.UmsToken;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.service.UmsUserCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 用户缓存操作Service实现类
 */
@Service
@Slf4j
public class UmsUserCacheServiceImpl implements UmsUserCacheService {
	//region properties
	private Cache<String, Object> cache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private Cache<String, Boolean> invalidTokenCache = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.maximumSize(1000)
			.build();
	private final String CACHE_KEY_TOKEN = "token:";
	private final String CACHE_KEY_USER = "user:";
	private final String CACHE_KEY_USER_DETAILS = "user_details:";
	//endregion

	@Override
	public UmsToken getTokenByValue(String value) {
		String key = CACHE_KEY_TOKEN + value;
		return (UmsToken) cache.getIfPresent(key);
	}

	@Override
	public boolean isTokenInvalidForSure(String token) {
		Boolean invalid = invalidTokenCache.getIfPresent(token);

		return invalid != null;
	}

	@Override
	public void removeToken(String token) {
		String key = CACHE_KEY_TOKEN + token;
		cache.invalidate(key);
	}

	@Override
	public void setTokenInvalid(String token) {
		invalidTokenCache.put(token, true);
	}

	@Override
	public void setToken(UmsToken umsToken) {
		String key = CACHE_KEY_TOKEN + umsToken.getValue();
		cache.put(key, umsToken);
	}

	@Override
	public UmsUser getUserById(Long userId) {
		String key = CACHE_KEY_USER + userId;
		return (UmsUser) cache.getIfPresent(key);
	}

	@Override
	public void removeUser(UmsUser user) {
		String userKey = CACHE_KEY_USER + user.getId();
		cache.invalidate(userKey);

		removeUserDetailsByUserId(user.getId());
	}

	@Override
	public void setUser(UmsUser user) {
		String key = CACHE_KEY_USER + user.getId();
		cache.put(key, user);
	}

	@Override
	public UmsUserDetails getUserDetailsByUserId(Long userId) {
		String key = CACHE_KEY_USER_DETAILS + userId;
		return (UmsUserDetails) cache.getIfPresent(key);
	}

	@Override
	public void removeUserDetailsByUserId(Long userId) {
		String key = CACHE_KEY_USER_DETAILS + userId;
		cache.invalidate(key);
	}

	@Override
	public void setUserDetails(UmsUserDetails userDetails) {
		String key = CACHE_KEY_USER_DETAILS + userDetails.getUserId();
		cache.put(key, userDetails);
	}

	@Override
	public void clearAll() {
		cache.invalidateAll();
		invalidTokenCache.invalidateAll();
	}
}
