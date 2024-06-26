package com.example.zhiyoufy.server.service;

import com.example.zhiyoufy.mbg.model.UmsToken;
import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;

/**
 * 用户缓存操作Service
 */
public interface UmsUserCacheService {
	// UmsToken
	UmsToken getTokenByValue(String value);

	boolean isTokenInvalidForSure(String token);

	void removeToken(String token);

	void setTokenInvalid(String token);

	void setToken(UmsToken umsToken);

	// UmsUser
	UmsUser getUserById(Long userId);

	void removeUser(UmsUser user);

	void setUser(UmsUser user);

	// UmsUserDetails
	UmsUserDetails getUserDetailsByUserId(Long userId);

	void removeUserDetailsByUserId(Long userId);

	void setUserDetails(UmsUserDetails userDetails);

	void clearAll();
}
