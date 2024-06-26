package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.EmsConfigCollectionMapper;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigCollectionExample;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionUpdateParam;
import com.example.zhiyoufy.server.mapstruct.EmsEnvironmentStructMapper;
import com.example.zhiyoufy.server.service.EmsConfigCollectionCacheService;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import com.example.zhiyoufy.server.service.EmsConfigItemService;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Getter
@Setter
@Slf4j
public class EmsConfigCollectionServiceImpl implements EmsConfigCollectionService {
	@Autowired
	EmsEnvironmentService environmentService;
	@Autowired
	UmsUserService userService;
	@Autowired
	EmsConfigItemService configItemService;

	@Autowired
	EmsConfigCollectionCacheService configCollectionCacheService;

	@Autowired
	EmsConfigCollectionMapper configCollectionMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public EmsConfigCollection addConfigCollection(
			EmsConfigCollectionParam configCollectionParam) {
		Assert.notNull(configCollectionParam, "configCollectionParam is null");
		Assert.notNull(configCollectionParam.getEnvironmentId(), "environmentId is null");
		Assert.hasText(configCollectionParam.getName(), "name is empty");

		Long envId = configCollectionParam.getEnvironmentId();

		EmsConfigCollection configCollection = getConfigCollectionByEnvIdAndName(
				envId, configCollectionParam.getName());

		if (configCollection != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		EmsEnvironment emsEnvironment = environmentService.getEnvironmentById(envId);

		if (emsEnvironment == null ||
				!emsEnvironment.getName().equals(configCollectionParam.getEnvironmentName())) {
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

		// add EmsConfigCollection
		configCollection = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigCollectionParamToEmsConfigCollection(configCollectionParam);
		configCollection.setCreatedBy(userDetails.getUsername());
		configCollection.setModifiedBy(userDetails.getUsername());

		configCollectionMapper.insertSelective(configCollection);

		return configCollection;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	@Transactional
	public int delConfigCollectionById(Long configCollectionId) {
		EmsConfigCollection configCollection = getConfigCollectionById(configCollectionId);

		if (configCollection == null) {
			return 0;
		}

		Long envId = configCollection.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete EmsConfigCollection
		configItemService.delConfigItemsByCollectionId(configCollectionId);
		int deleted = configCollectionMapper.deleteByPrimaryKey(configCollectionId);

		configCollectionCacheService.removeConfigCollection(configCollection);

		return deleted;
	}

	@Override
	public int delConfigCollectionsByEnvId(Long envId) {
		configItemService.delConfigItemsByEnvId(envId);

		EmsConfigCollectionExample example = new EmsConfigCollectionExample();
		EmsConfigCollectionExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId);

		int deleted = configCollectionMapper.deleteByExample(example);

		configCollectionCacheService.clearAll();

		return deleted;
	}

	@Override
	public EmsConfigCollection getConfigCollectionById(Long configCollectionId) {
		EmsConfigCollection configCollection =
				configCollectionCacheService.getConfigCollectionById(configCollectionId);

		if (configCollection != null) {
			return configCollection;
		}

		configCollection = configCollectionMapper.selectByPrimaryKey(configCollectionId);

		if (configCollection != null) {
			configCollectionCacheService.setConfigCollection(configCollection);
		}

		return configCollection;
	}

	@Override
	public EmsConfigCollection getConfigCollectionByEnvIdAndName(Long envId, String name) {
		EmsConfigCollection configCollection =
				configCollectionCacheService.getConfigCollectionByEnvIdAndName(envId, name);

		if (configCollection != null) {
			return configCollection;
		}

		configCollection = getConfigCollectionByEnvIdAndNameFromDb(envId, name);

		return configCollection;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor') "
			+ "|| hasAuthority('roles/environment.viewer')")
	public CommonPage<EmsConfigCollection> getConfigCollectionList(Long envId,
			EmsConfigCollectionQueryParam queryParam, Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigCollectionExample example = new EmsConfigCollectionExample();
		EmsConfigCollectionExample.Criteria criteria = example.createCriteria();

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

		List<EmsConfigCollection> configCollectionList =
				configCollectionMapper.selectByExample(example);

		return CommonPage.restPage(configCollectionList);
	}

	@Override
	public List<EmsConfigCollection> getConfigCollectionList(List<Long> envIdList,
			List<String> collectionNameList) {
		EmsConfigCollectionExample example = new EmsConfigCollectionExample();
		EmsConfigCollectionExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdIn(envIdList);
		criteria.andNameIn(collectionNameList);

		List<EmsConfigCollection> configCollectionList =
				configCollectionMapper.selectByExample(example);

		return configCollectionList;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public int updateConfigCollection(Long id, EmsConfigCollectionUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		EmsConfigCollection targetConfigCollection = getConfigCollectionById(id);

		if (targetConfigCollection == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long envId = targetConfigCollection.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigCollection configCollection = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigCollectionUpdateParamToEmsConfigCollection(updateParam);
		configCollection.setId(id);
		configCollection.setModifiedBy(userDetails.getUsername());

		int updated = configCollectionMapper.updateByPrimaryKeySelective(configCollection);

		configCollectionCacheService.removeConfigCollection(targetConfigCollection);

		return updated;
	}

	private EmsConfigCollection getConfigCollectionByEnvIdAndNameFromDb(Long envId, String name) {
		EmsConfigCollectionExample example = new EmsConfigCollectionExample();
		EmsConfigCollectionExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId).andNameEqualTo(name);

		List<EmsConfigCollection> configCollectionList =
				configCollectionMapper.selectByExample(example);

		if (configCollectionList != null && configCollectionList.size() > 0) {
			EmsConfigCollection configCollection = configCollectionList.get(0);

			configCollectionCacheService.setConfigCollection(configCollection);

			return configCollection;
		}

		return null;
	}
}
