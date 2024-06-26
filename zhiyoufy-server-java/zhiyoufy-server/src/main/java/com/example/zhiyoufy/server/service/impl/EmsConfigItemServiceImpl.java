package com.example.zhiyoufy.server.service.impl;

import java.util.Date;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.EmsConfigItemMapper;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsConfigItemExample;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.dao.EmsConfigItemDao;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParamForJob;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemUpdateParam;
import com.example.zhiyoufy.server.mapstruct.EmsEnvironmentStructMapper;
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
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Getter
@Setter
@Slf4j
public class EmsConfigItemServiceImpl implements EmsConfigItemService {
	@Autowired
	EmsEnvironmentService environmentService;
	@Autowired
	UmsUserService userService;
	@Autowired
	EmsConfigCollectionService configCollectionService;
	@Autowired
	EmsConfigItemDao configItemDao;

	@Autowired
	EmsConfigItemMapper configItemMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public EmsConfigItem addConfigItem(EmsConfigItemParam configItemParam) {
		Assert.notNull(configItemParam, "configItemParam is null");
		Assert.notNull(configItemParam.getEnvironmentId(), "environmentId is null");
		Assert.notNull(configItemParam.getCollectionId(), "collectionId is null");
		Assert.hasText(configItemParam.getName(), "name is empty");

		Long envId = configItemParam.getEnvironmentId();
		Long collectionId = configItemParam.getCollectionId();

		EmsConfigItem configItem = getConfigItemByCollectionIdAndName(
				collectionId, configItemParam.getName());

		if (configItem != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		EmsEnvironment emsEnvironment = environmentService.getEnvironmentById(envId);

		if (emsEnvironment == null ||
				!emsEnvironment.getName().equals(configItemParam.getEnvironmentName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		EmsConfigCollection emsConfigCollection =
				configCollectionService.getConfigCollectionById(collectionId);

		if (emsConfigCollection == null ||
				!emsConfigCollection.getName().equals(configItemParam.getCollectionName())
				|| !emsConfigCollection.getEnvironmentId().equals(envId)) {
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

		// add EmsConfigItem
		configItem = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigItemParamToEmsConfigItem(configItemParam);

		if (configItem.getConfigValue() == null) {
			configItem.setConfigValue("");
		}

		configItemMapper.insertSelective(configItem);

		return configItem;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public int delConfigItemById(Long configItemId) {
		EmsConfigItem configItem = getConfigItemById(configItemId);

		if (configItem == null) {
			return 0;
		}

		Long envId = configItem.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete EmsConfigItem
		int deleted = configItemMapper.deleteByPrimaryKey(configItemId);

		return deleted;
	}

	@Override
	public int delConfigItemsByCollectionId(Long collectionId) {
		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andCollectionIdEqualTo(collectionId);

		int deleted = configItemMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public int delConfigItemsByEnvId(Long envId) {
		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andEnvironmentIdEqualTo(envId);

		int deleted = configItemMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public EmsConfigItem getConfigItemById(Long configItemId) {
		EmsConfigItem configItem =
				configItemMapper.selectByPrimaryKey(configItemId);

		return configItem;
	}

	@Override
	public EmsConfigItem getConfigItemByCollectionIdAndName(Long collectionId, String name) {
		EmsConfigItem configItem =
				getConfigItemByCollectionIdAndNameFromDb(collectionId, name);

		return configItem;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor') "
			+ "|| hasAuthority('roles/environment.viewer')")
	public CommonPage<EmsConfigItem> getConfigItemList(Long collectionId,
			EmsConfigItemQueryParam queryParam, Integer pageSize, Integer pageNum) {
		EmsConfigCollection emsConfigCollection =
				configCollectionService.getConfigCollectionById(collectionId);

		if (emsConfigCollection == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long envId = emsConfigCollection.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andCollectionIdEqualTo(collectionId);

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

		List<EmsConfigItem> configItemList = configItemMapper.selectByExampleWithBLOBs(example);

		return CommonPage.restPage(configItemList);
	}

	@Override
	public List<EmsConfigItem> getConfigItemList(Long collectionId,
			EmsConfigItemQueryParamForJob queryParam) {
		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andCollectionIdEqualTo(collectionId);
		criteria.andDisabledEqualTo(false);
		criteria.andInUseEqualTo(false);

		if (StringUtils.hasText(queryParam.getIncludeTags())) {
			String[] tagList = queryParam.getIncludeTags().split("\\\\s*,\\\\s*");
			for (String tag : tagList) {
				criteria.andTagsLike("<" + tag + ">");
			}
		}

		if (StringUtils.hasText(queryParam.getExcludeTags())) {
			String[] tagList = queryParam.getExcludeTags().split("\\\\s*,\\\\s*");
			for (String tag : tagList) {
				criteria.andTagsNotLike("<" + tag + ">");
			}
		}

		PageHelper.startPage(1, queryParam.getRunNum());

		List<EmsConfigItem> configItemList = configItemMapper.selectByExampleWithBLOBs(example);

		return configItemList;
	}

	@Override
	public List<String> getRunIdListByInUse() {
		return configItemDao.getRunIdListByInUse();
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner') "
			+ "|| hasAuthority('roles/environment.editor')")
	public int updateConfigItem(Long id, EmsConfigItemUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		EmsConfigItem targetConfigItem = getConfigItemById(id);

		if (targetConfigItem == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long envId = targetConfigItem.getEnvironmentId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation relation = environmentService
					.getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsConfigItem configItem = EmsEnvironmentStructMapper.INSTANCE
				.emsConfigItemUpdateParamToEmsConfigItem(updateParam);
		configItem.setId(id);

		return configItemMapper.updateByPrimaryKeySelective(configItem);
	}

	@Override
	public int updateConfigItemsForAlloc(List<Long> idList, String usageId, Date usageTimeoutAt) {
		EmsConfigItem record = new EmsConfigItem();
		record.setInUse(true);
		record.setUsageId(usageId);
		record.setUsageTimeoutAt(usageTimeoutAt);

		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andIdIn(idList);

		return configItemMapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateConfigItemsForFreeByIdList(List<Long> idList) {
		EmsConfigItem record = new EmsConfigItem();
		record.setInUse(false);

		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andIdIn(idList);

		return configItemMapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateConfigItemsForFreeByUsageTimeoutAt(Date usageTimeoutAt) {
		EmsConfigItem record = new EmsConfigItem();
		record.setInUse(false);

		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andInUseEqualTo(true);
		criteria.andUsageTimeoutAtLessThan(usageTimeoutAt);

		return configItemMapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateConfigItemsForFreeByUsageId(String usageId) {
		EmsConfigItem record = new EmsConfigItem();
		record.setInUse(false);

		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andInUseEqualTo(true);
		criteria.andUsageIdEqualTo(usageId);

		return configItemMapper.updateByExampleSelective(record, example);
	}

	private EmsConfigItem getConfigItemByCollectionIdAndNameFromDb(Long collectionId, String name) {
		EmsConfigItemExample example = new EmsConfigItemExample();
		EmsConfigItemExample.Criteria criteria = example.createCriteria();

		criteria.andCollectionIdEqualTo(collectionId).andNameEqualTo(name);

		List<EmsConfigItem> configItemList = configItemMapper.selectByExampleWithBLOBs(example);

		if (configItemList != null && configItemList.size() > 0) {
			EmsConfigItem configItem = configItemList.get(0);

			return configItem;
		}

		return null;
	}
}
