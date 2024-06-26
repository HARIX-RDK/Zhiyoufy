package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.WmsWorkerGroupMapper;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroupExample;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupUpdateParam;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
import com.example.zhiyoufy.server.service.WmsWorkerGroupCacheService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
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
public class WmsWorkerGroupServiceImpl implements WmsWorkerGroupService {
	@Autowired
	WmsWorkerAppService workerAppService;
	@Autowired
	UmsUserService userService;

	@Autowired
	WmsWorkerGroupCacheService workerGroupCacheService;

	@Autowired
	WmsWorkerGroupMapper workerGroupMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	public WmsWorkerGroup addWorkerGroup(
			WmsWorkerGroupParam workerGroupParam) {
		Assert.notNull(workerGroupParam, "workerGroupParam is null");
		Assert.notNull(workerGroupParam.getWorkerAppId(), "workerAppId is null");
		Assert.hasText(workerGroupParam.getName(), "name is empty");

		Long workerAppId = workerGroupParam.getWorkerAppId();

		WmsWorkerApp wmsWorkerApp = workerAppService.getWorkerAppById(workerAppId);

		if (wmsWorkerApp == null ||
				!wmsWorkerApp.getName().equals(workerGroupParam.getWorkerAppName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		WmsWorkerGroup workerGroup = getWorkerGroupByWorkerAppIdAndName(
				workerAppId, workerGroupParam.getName());

		if (workerGroup != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// add WmsWorkerGroup
		workerGroup = WmsWorkerAppStructMapper.INSTANCE
				.wmsWorkerGroupParamToWmsWorkerGroup(workerGroupParam);
		workerGroup.setCreatedBy(userDetails.getUsername());
		workerGroup.setModifiedBy(userDetails.getUsername());

		workerGroupMapper.insertSelective(workerGroup);

		return workerGroup;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	@Transactional
	public DeleteInfo delWorkerGroupById(Long workerGroupId) {
		WmsWorkerGroup workerGroup = getWorkerGroupById(workerGroupId);

		if (workerGroup == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long workerAppId = workerGroup.getWorkerAppId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete WmsWorkerGroup
//		configItemService.delConfigItwmsByCollectionId(workerGroupId);
		int deleted = workerGroupMapper.deleteByPrimaryKey(workerGroupId);

		workerGroupCacheService.removeWorkerGroup(workerGroup);

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(workerGroup.getName())
				.build();

		return deleteInfo;
	}

	@Override
	public int delWorkerGroupsByWorkerAppId(Long workerAppId) {
//		configItemService.delConfigItwmsByWorkerAppId(workerAppId);

		WmsWorkerGroupExample example = new WmsWorkerGroupExample();
		WmsWorkerGroupExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerAppIdEqualTo(workerAppId);

		int deleted = workerGroupMapper.deleteByExample(example);

		workerGroupCacheService.clearAll();

		return deleted;
	}

	@Override
	public WmsWorkerGroup getWorkerGroupById(Long workerGroupId) {
		WmsWorkerGroup workerGroup =
				workerGroupCacheService.getWorkerGroupById(workerGroupId);

		if (workerGroup != null) {
			return workerGroup;
		}

		workerGroup = workerGroupMapper.selectByPrimaryKey(workerGroupId);

		if (workerGroup != null) {
			workerGroupCacheService.setWorkerGroup(workerGroup);
		}

		return workerGroup;
	}

	@Override
	public WmsWorkerGroup getWorkerGroupByWorkerAppIdAndName(Long workerAppId, String name) {
		WmsWorkerGroup workerGroup =
				workerGroupCacheService.getWorkerGroupByWorkerAppIdAndName(workerAppId, name);

		if (workerGroup != null) {
			return workerGroup;
		}

		workerGroup = getWorkerGroupByWorkerAppIdAndNameFromDb(workerAppId, name);

		return workerGroup;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor') "
			+ "|| hasAuthority('roles/workerApp.viewer')")
	public CommonPage<WmsWorkerGroup> getWorkerGroupList(Long workerAppId,
			WmsWorkerGroupQueryParam queryParam, Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsWorkerGroupExample example = new WmsWorkerGroupExample();
		WmsWorkerGroupExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerAppIdEqualTo(workerAppId);

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

		List<WmsWorkerGroup> workerGroupList =
				workerGroupMapper.selectByExample(example);

		return CommonPage.restPage(workerGroupList);
	}

	@Override
	public List<WmsWorkerGroupBase> getWorkerGroupBaseList() {
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	public UpdateInfo updateWorkerGroup(Long id, WmsWorkerGroupUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		WmsWorkerGroup targetWorkerGroup = getWorkerGroupById(id);

		if (targetWorkerGroup == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long workerAppId = targetWorkerGroup.getWorkerAppId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsWorkerGroup workerGroup = WmsWorkerAppStructMapper.INSTANCE
				.wmsWorkerGroupUpdateParamToWmsWorkerGroup(updateParam);
		workerGroup.setId(id);
		workerGroup.setModifiedBy(userDetails.getUsername());

		int updated = workerGroupMapper.updateByPrimaryKeySelective(workerGroup);

		workerGroupCacheService.removeWorkerGroup(targetWorkerGroup);

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetWorkerGroup.getName())
				.build();

		return updateInfo;
	}

	private WmsWorkerGroup getWorkerGroupByWorkerAppIdAndNameFromDb(Long workerAppId, String name) {
		WmsWorkerGroupExample example = new WmsWorkerGroupExample();
		WmsWorkerGroupExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerAppIdEqualTo(workerAppId).andNameEqualTo(name);

		List<WmsWorkerGroup> workerGroupList =
				workerGroupMapper.selectByExample(example);

		if (workerGroupList != null && workerGroupList.size() > 0) {
			WmsWorkerGroup workerGroup = workerGroupList.get(0);

			workerGroupCacheService.setWorkerGroup(workerGroup);

			return workerGroup;
		}

		return null;
	}
}
