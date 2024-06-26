package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.EmsConfigSingleMapper;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.mbg.model.EmsConfigSingleExample;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleUpdateParam;
import com.example.zhiyoufy.server.mapstruct.EmsEnvironmentStructMapper;
import com.example.zhiyoufy.server.service.EmsConfigSingleCacheService;
import com.example.zhiyoufy.server.service.EmsConfigSingleService;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Getter
@Setter
@Slf4j
public class EmsConfigSingleServiceImpl implements EmsConfigSingleService {
	@Autowired
	EmsEnvironmentService environmentService;
	@Autowired
	UmsUserService userService;

	@Autowired
	EmsConfigSingleCacheService configSingleCacheService;

	@Autowired
	EmsConfigSingleMapper configSingleMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') || hasAuthority('roles/environment.editor')")
	public EmsConfigSingle addConfigSingle(EmsConfigSingleParam configSingleParam) {
		Assert.notNull(configSingleParam, "configSingleParam is null");
		Assert.notNull(configSingleParam.getEnvironmentId(), "environmentId is null");
		Assert.hasText(configSingleParam.getName(), "name is empty");

		Long envId = configSingleParam.getEnvironmentId();

		EmsConfigSingle configSingle = getConfigSingleByEnvIdAndName(
				envId, configSingleParam.getName());

		if (configSingle != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		EmsEnvironment emsEnvironment = environmentService.getEnvironmentById(envId);

		if (emsEnvironment == null ||
				!emsEnvironment.getName().equals(configSingleParam.getEnvironmentName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// add EmsConfigSingle
		configSingle = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigSingleParamToEmsConfigSingle(configSingleParam);
		configSingle.setCreatedBy(userDetails.getUsername());
		configSingle.setModifiedBy(userDetails.getUsername());

		if (configSingle.getConfigValue() == null) {
			configSingle.setConfigValue("");
		}

		configSingleMapper.insertSelective(configSingle);

		return configSingle;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public Integer delConfigSingleById(Long configSingleId) {
		EmsConfigSingle configSingle = getConfigSingleById(configSingleId);

		if (configSingle == null) {
			return 0;
		}

		Long envId = configSingle.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete EmsConfigSingle
		int deleted = configSingleMapper.deleteByPrimaryKey(configSingleId);

		configSingleCacheService.removeConfigSingle(configSingle);

		return deleted;
	}

	@Override
	public int delConfigSinglesByEnvId(Long envId) {
		EmsConfigSingleExample example = new EmsConfigSingleExample();
		EmsConfigSingleExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId);

		int deleted = configSingleMapper.deleteByExample(example);

		configSingleCacheService.clearAll();

		return deleted;
	}

	@Override
	public EmsConfigSingle getConfigSingleById(Long configSingleId) {
		EmsConfigSingle configSingle =
				configSingleCacheService.getConfigSingleById(configSingleId);

		if (configSingle != null) {
			return configSingle;
		}

		configSingle = configSingleMapper.selectByPrimaryKey(configSingleId);

		if (configSingle != null) {
			configSingleCacheService.setConfigSingle(configSingle);
		}

		return configSingle;
	}

	@Override
	public EmsConfigSingle getConfigSingleByEnvIdAndName(Long envId, String name) {
		EmsConfigSingle configSingle =
				configSingleCacheService.getConfigSingleByEnvIdAndName(envId, name);

		if (configSingle != null) {
			return configSingle;
		}

		configSingle = getConfigSingleByEnvIdAndNameFromDb(envId, name);

		return configSingle;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor') "
			+ "|| hasAuthority('roles/environment.viewer')")
	public CommonPage<EmsConfigSingle> getConfigSingleList(Long envId,
			EmsConfigSingleQueryParam queryParam, Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigSingleExample example = new EmsConfigSingleExample();
		EmsConfigSingleExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId);

		if (StringUtils.hasText(queryParam.getKeyword())) {
			if (CheckUtils.isTrue(queryParam.getExactMatch())) {
				criteria.andNameLike(queryParam.getKeyword());
			} else {
				criteria.andNameLike("%" + queryParam.getKeyword() + "%");
			}
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			example.setOrderByClause(orderBy);
		}

		PageHelper.startPage(pageNum, pageSize);

		List<EmsConfigSingle> configSingleList = configSingleMapper.selectByExampleWithBLOBs(example);

		return CommonPage.restPage(configSingleList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public int updateConfigSingle(Long id, EmsConfigSingleUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		EmsConfigSingle targetConfigSingle = getConfigSingleById(id);

		if (targetConfigSingle == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long envId = targetConfigSingle.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigSingle configSingle = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigSingleUpdateParamToEmsConfigSingle(updateParam);
		configSingle.setId(id);
		configSingle.setModifiedBy(userDetails.getUsername());

		configSingleCacheService.removeConfigSingle(targetConfigSingle);

		return configSingleMapper.updateByPrimaryKeySelective(configSingle);
	}

	private EmsConfigSingle getConfigSingleByEnvIdAndNameFromDb(Long envId, String name) {
		EmsConfigSingleExample example = new EmsConfigSingleExample();
		EmsConfigSingleExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId).andNameEqualTo(name);

		List<EmsConfigSingle> configSingleList = configSingleMapper.selectByExampleWithBLOBs(example);

		if (configSingleList != null && configSingleList.size() > 0) {
			EmsConfigSingle configSingle = configSingleList.get(0);

			configSingleCacheService.setConfigSingle(configSingle);

			return configSingle;
		}

		return null;
	}
}
