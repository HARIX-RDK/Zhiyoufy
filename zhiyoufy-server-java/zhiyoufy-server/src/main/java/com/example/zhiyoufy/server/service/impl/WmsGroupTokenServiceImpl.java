package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.WmsGroupTokenMapper;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsGroupTokenExample;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenUpdateParam;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.service.WmsGroupTokenService;
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
public class WmsGroupTokenServiceImpl implements WmsGroupTokenService {
	@Autowired
	UmsUserService userService;
	@Autowired
	WmsWorkerAppService workerAppService;
	@Autowired
	WmsWorkerGroupService workerGroupService;

	@Autowired
	WmsGroupTokenMapper groupTokenMapper;

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	public WmsGroupToken addGroupToken(WmsGroupTokenParam groupTokenParam) {
		Assert.notNull(groupTokenParam, "groupTokenParam is null");
		Assert.notNull(groupTokenParam.getWorkerAppId(), "workerAppId is null");
		Assert.notNull(groupTokenParam.getWorkerGroupId(), "workerGroupId is null");
		Assert.hasText(groupTokenParam.getName(), "name is empty");

		Long workerAppId = groupTokenParam.getWorkerAppId();
		Long workerGroupId = groupTokenParam.getWorkerGroupId();

		WmsGroupToken groupToken = getGroupTokenByWorkerGroupIdAndName(
				workerGroupId, groupTokenParam.getName());

		if (groupToken != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		WmsWorkerApp workerApp = workerAppService.getWorkerAppById(workerAppId);

		if (workerApp == null ||
				!workerApp.getName().equals(groupTokenParam.getWorkerAppName())) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		WmsWorkerGroup workerGroup =
				workerGroupService.getWorkerGroupById(workerGroupId);

		if (workerGroup == null ||
				!workerGroup.getName().equals(groupTokenParam.getWorkerGroupName())
				|| !workerGroup.getWorkerAppId().equals(workerAppId)) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(
							workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// add WmsGroupToken
		groupToken = WmsWorkerAppStructMapper.INSTANCE
				.wmsGroupTokenParamToWmsGroupToken(groupTokenParam);
		groupToken.setCreatedBy(userDetails.getUsername());
		groupToken.setModifiedBy(userDetails.getUsername());

		groupTokenMapper.insertSelective(groupToken);

		return groupToken;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	public DeleteInfo delGroupTokenById(Long groupTokenId) {
		WmsGroupToken groupToken = getGroupTokenById(groupTokenId);

		if (groupToken == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long workerAppId = groupToken.getWorkerAppId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete WmsGroupToken
		int deleted = groupTokenMapper.deleteByPrimaryKey(groupTokenId);

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(groupToken.getName())
				.build();

		return deleteInfo;
	}

	@Override
	public int delGroupTokensByWorkerGroupId(Long workerGroupId) {
		WmsGroupTokenExample example = new WmsGroupTokenExample();
		WmsGroupTokenExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerGroupIdEqualTo(workerGroupId);

		int deleted = groupTokenMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public int delGroupTokensByWorkerAppId(Long workerAppId) {
		WmsGroupTokenExample example = new WmsGroupTokenExample();
		WmsGroupTokenExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerAppIdEqualTo(workerAppId);

		int deleted = groupTokenMapper.deleteByExample(example);

		return deleted;
	}

	@Override
	public WmsGroupToken getGroupTokenById(Long groupTokenId) {
		WmsGroupToken groupToken =
				groupTokenMapper.selectByPrimaryKey(groupTokenId);

		return groupToken;
	}

	@Override
	public WmsGroupToken getGroupTokenByWorkerGroupIdAndName(Long workerGroupId, String name) {
		WmsGroupToken groupToken =
				getGroupTokenByWorkerGroupIdAndNameFromDb(workerGroupId, name);

		return groupToken;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor') ")
	public CommonPage<WmsGroupToken> getGroupTokenList(Long workerGroupId,
			WmsGroupTokenQueryParam queryParam, Integer pageSize, Integer pageNum) {
		WmsWorkerGroup wmsWorkerGroup =
				workerGroupService.getWorkerGroupById(workerGroupId);

		if (wmsWorkerGroup == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long workerAppId = wmsWorkerGroup.getWorkerAppId();

		UmsUserDetails userDetails = userService.getUserDetails();
		WmsWorkerAppUserRelation relation = null;

		if (!userDetails.isAdmin()) {
			relation = workerAppService.getWorkerAppUserRelationByWorkerAppIdAndUserId(
					workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsGroupTokenExample example = new WmsGroupTokenExample();
		WmsGroupTokenExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerGroupIdEqualTo(workerGroupId);

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

		List<WmsGroupToken> groupTokenList = groupTokenMapper.selectByExample(example);

		return CommonPage.restPage(groupTokenList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner') "
			+ "|| hasAuthority('roles/workerApp.editor')")
	public UpdateInfo updateGroupToken(Long id, WmsGroupTokenUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		WmsGroupToken targetGroupToken = getGroupTokenById(id);

		if (targetGroupToken == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		Long workerAppId = targetGroupToken.getWorkerAppId();

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation relation = workerAppService
					.getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
					CheckUtils.isTrue(relation.getIsEditor()))) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsGroupToken groupToken = WmsWorkerAppStructMapper.INSTANCE
				.wmsGroupTokenUpdateParamToWmsGroupToken(updateParam);
		groupToken.setId(id);
		groupToken.setModifiedBy(userDetails.getUsername());

		int updated = groupTokenMapper.updateByPrimaryKeySelective(groupToken);

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetGroupToken.getName())
				.build();

		return updateInfo;
	}

	private WmsGroupToken getGroupTokenByWorkerGroupIdAndNameFromDb(Long workerGroupId, String name) {
		WmsGroupTokenExample example = new WmsGroupTokenExample();
		WmsGroupTokenExample.Criteria criteria = example.createCriteria();

		criteria.andWorkerGroupIdEqualTo(workerGroupId).andNameEqualTo(name);

		List<WmsGroupToken> groupTokenList = groupTokenMapper.selectByExample(example);

		if (groupTokenList != null && groupTokenList.size() > 0) {
			WmsGroupToken groupToken = groupTokenList.get(0);

			return groupToken;
		}

		return null;
	}
}
