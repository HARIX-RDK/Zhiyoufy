package com.example.zhiyoufy.server.dao;

import java.util.List;

import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDaoQueryParam;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserBase;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import org.apache.ibatis.annotations.Param;

public interface UmsUserDao {
	List<UmsUserBase> getUserBaseList(@Param("queryParam") UmsUserDaoQueryParam queryParam);

	List<UmsUserDTO> getUserList(@Param("queryParam") UmsUserDaoQueryParam queryParam);
}
