package com.example.zhiyoufy.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.WmsWorkerAppMapper;
import com.example.zhiyoufy.mbg.mapper.WmsWorkerAppUserRelationMapper;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppExample;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelationExample;
import com.example.zhiyoufy.server.dao.WmsWorkerAppDao;
import com.example.zhiyoufy.server.dao.WmsWorkerAppUserRelationDao;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.bo.wms.WmsWorkerAppDaoQueryParam;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppQueryParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationUpdateParam;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.service.WmsWorkerAppCacheService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
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
public class WmsWorkerAppServiceImpl implements WmsWorkerAppService {
	@Autowired
	WmsWorkerAppMapper workerAppMapper;
	@Autowired
	WmsWorkerAppUserRelationMapper workerAppUserRelationMapper;

	@Autowired
	WmsWorkerAppDao workerAppDao;
	@Autowired
	WmsWorkerAppUserRelationDao workerAppUserRelationDao;

	@Autowired
	WmsWorkerAppCacheService workerAppCacheService;
	@Autowired
	UmsUserService userService;
	
	@Override
	@PreAuthorize("hasAuthority('workerApps.create')")
	@Transactional
	public WmsWorkerApp addWorkerApp(WmsWorkerAppParam workerAppParam) {
		Assert.notNull(workerAppParam, "workerAppParam is null");
		Assert.hasText(workerAppParam.getName(), "name is empty");

		WmsWorkerApp wmsWorkerApp = getWorkerAppByName(workerAppParam.getName());

		if (wmsWorkerApp != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		// add WmsWorkerApp
		wmsWorkerApp = WmsWorkerAppStructMapper.INSTANCE
				.wmsWorkerAppParamToWmsWorkerApp(workerAppParam);
		wmsWorkerApp.setCreatedBy(userDetails.getUsername());
		wmsWorkerApp.setModifiedBy(userDetails.getUsername());

		workerAppMapper.insertSelective(wmsWorkerApp);

		// add WmsWorkerAppUserRelation
		WmsWorkerAppUserRelation workerAppUserRelation = new WmsWorkerAppUserRelation();
		workerAppUserRelation.setWorkerAppId(wmsWorkerApp.getId());
		workerAppUserRelation.setUserId(userDetails.getUserId());
		workerAppUserRelation.setIsOwner(true);

		workerAppUserRelationMapper.insertSelective(workerAppUserRelation);

		// select to include DB generated time values
		wmsWorkerApp = workerAppMapper.selectByPrimaryKey(wmsWorkerApp.getId());

		workerAppCacheService.clearAll();

		return wmsWorkerApp;
	}

	@Override
	@PreAuthorize("hasAuthority('workerApps.delete')")
	@Transactional
	public DeleteInfo delWorkerAppById(Long workerAppId) {
		WmsWorkerApp workerApp = getWorkerAppById(workerAppId);

		if (workerApp == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation workerAppUserRelation =
					getWorkerAppUserRelationByWorkerAppIdAndUserId(workerAppId, userDetails.getUserId());

			if (workerAppUserRelation == null || !workerAppUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete WmsWorkerAppUserRelation
		{
			WmsWorkerAppUserRelationExample example = new WmsWorkerAppUserRelationExample();
			WmsWorkerAppUserRelationExample.Criteria criteria = example
					.createCriteria();

			criteria.andWorkerAppIdEqualTo(workerAppId);

			workerAppUserRelationMapper.deleteByExample(example);
		}

		// delete WmsWorkerApp
		int deleted = workerAppMapper.deleteByPrimaryKey(workerAppId);

		workerAppCacheService.clearAll();

		DeleteInfo deleteInfo = DeleteInfo.builder()
				.deleted(deleted)
				.name(workerApp.getName())
				.build();

		return deleteInfo;
	}

	@Override
	public WmsWorkerApp getWorkerAppById(Long workerAppId) {
		WmsWorkerApp workerApp = workerAppCacheService.getWorkerAppById(workerAppId);

		if (workerApp != null) {
			return workerApp;
		}

		workerApp = workerAppMapper.selectByPrimaryKey(workerAppId);

		if (workerApp != null) {
			workerAppCacheService.setWorkerApp(workerApp);
		}

		return workerApp;
	}

	@Override
	public WmsWorkerApp getWorkerAppByName(String name) {
		WmsWorkerApp wmsWorkerApp = getWorkerAppByNameFromDb(name);

		return wmsWorkerApp;
	}

	@Override
	@PreAuthorize("hasAuthority('workerApps.get')")
	public CommonPage<WmsWorkerAppFull> getWorkerAppList(
			WmsWorkerAppQueryParam queryParam, Integer pageSize, Integer pageNum) {
		if (!CheckUtils.isTrue(queryParam.getAllUsers())) {
			return getWorkerAppListForCurUser(queryParam, pageSize, pageNum);
		}

		return getWorkerAppListForAllUsers(queryParam, pageSize, pageNum);
	}

	@Override
	public List<WmsWorkerAppBase> getWorkerAppBaseList() {
		UmsUserDetails userDetails = userService.getUserDetails();

		List<WmsWorkerAppBase> workerAppBaseList =
				workerAppDao.getWorkerAppBaseListByUserId(userDetails.getUserId());

		return workerAppBaseList;
	}

	@Override
	@PreAuthorize("hasAuthority('workerApps.update')")
	public UpdateInfo updateWorkerApp(Long id, WmsWorkerAppUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		WmsWorkerApp targetWorkerApp = getWorkerAppById(id);

		if (targetWorkerApp == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation workerAppUserRelation =
					getWorkerAppUserRelationByWorkerAppIdAndUserId(id, userDetails.getUserId());

			if (workerAppUserRelation == null || (!workerAppUserRelation.getIsOwner() &&
					!workerAppUserRelation.getIsEditor())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsWorkerApp wmsWorkerApp = WmsWorkerAppStructMapper.INSTANCE
				.wmsWorkerAppUpdateParamToWmsWorkerApp(updateParam);
		wmsWorkerApp.setId(id);
		wmsWorkerApp.setModifiedBy(userDetails.getUsername());

		int updated = workerAppMapper.updateByPrimaryKeySelective(wmsWorkerApp);

		workerAppCacheService.clearAll();

		UpdateInfo updateInfo = UpdateInfo.builder()
				.updated(updated)
				.name(targetWorkerApp.getName())
				.build();

		return updateInfo;
	}

	@Override
	public WmsWorkerAppUserRelation getWorkerAppUserRelationByWorkerAppIdAndUserId(
			Long workerAppId, Long userId) {
		Map<Long, WmsWorkerAppUserRelation> relationMap =
				getWorkerAppUserRelationMapByUserId(userId);

		return relationMap.get(workerAppId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner')")
	public int addWorkerAppUserRelation(WmsWorkerAppUserRelationFull relationFull) {
		Assert.isNull(relationFull.getId(), "id should be null");

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation curUserRelation =
					getWorkerAppUserRelationByWorkerAppIdAndUserId(
							relationFull.getWorkerAppId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsWorkerAppUserRelation relation = WmsWorkerAppStructMapper.INSTANCE
				.relationFullToRelation(relationFull);
		relation.setId(null);
		int cnt = workerAppUserRelationMapper.insert(relation);

		workerAppCacheService.clearAll();

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner')")
	@Transactional
	public int delWorkerAppUserRelationById(Long id) {
		WmsWorkerAppUserRelation relation =
				workerAppUserRelationMapper.selectByPrimaryKey(id);

		if (relation == null) {
			Asserts.fail(CommonErrorCode.RES_NOT_FOUND);
		}

		if (relation.getIsOwner()) {
			WmsWorkerAppUserRelationExample example =
					new WmsWorkerAppUserRelationExample();
			WmsWorkerAppUserRelationExample.Criteria criteria = example.createCriteria();

			criteria.andWorkerAppIdEqualTo(relation.getWorkerAppId())
					.andIsOwnerEqualTo(true);

			long ownerCnt = workerAppUserRelationMapper.countByExample(example);

			if (ownerCnt == 1) {
				Asserts.fail(CommonErrorCode.RES_DELETE_LAST_OWNER_NOT_ALLOWED);
			}
		}

		int cnt = workerAppUserRelationMapper.deleteByPrimaryKey(id);

		workerAppCacheService.clearAll();

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner')")
	public CommonPage<WmsWorkerAppUserRelationFull> getWorkerAppUserRelationListByWorkerAppId(
			Long workerAppId, Integer pageSize, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);

		List<WmsWorkerAppUserRelationFull> relationFullList =
				workerAppUserRelationDao.getUserRelationListByWorkerAppId(workerAppId);

		return CommonPage.restPage(relationFullList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/workerApp.owner')")
	public int updateWorkerAppUserRelation(Long id, WmsWorkerAppUserRelationUpdateParam updateParam) {
		WmsWorkerAppUserRelation targetRelation = workerAppUserRelationMapper.selectByPrimaryKey(id);

		if (targetRelation == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			WmsWorkerAppUserRelation curUserRelation =
					getWorkerAppUserRelationByWorkerAppIdAndUserId(
							targetRelation.getWorkerAppId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		WmsWorkerAppUserRelation relation = WmsWorkerAppStructMapper.INSTANCE
				.updateParamToRelation(updateParam);
		relation.setId(id);

		workerAppCacheService.clearAll();

		return workerAppUserRelationMapper.updateByPrimaryKeySelective(relation);
	}

	private CommonPage<WmsWorkerAppFull> getWorkerAppListForAllUsers(
			WmsWorkerAppQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		WmsWorkerAppExample example = new WmsWorkerAppExample();
		WmsWorkerAppExample.Criteria criteria = example.createCriteria();

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

		List<WmsWorkerApp> workerAppList =
				workerAppMapper.selectByExample(example);
		List<WmsWorkerAppFull> workerAppFullList = workerAppList.stream()
				.map(workerApp -> {
					WmsWorkerAppFull workerAppFull = WmsWorkerAppStructMapper.INSTANCE
							.wmsWorkerAppToWmsWorkerAppFull(workerApp);
					return workerAppFull;
				})
				.collect(Collectors.toList());

		return CommonPage.restPage(workerAppFullList, workerAppList);
	}

	private CommonPage<WmsWorkerAppFull> getWorkerAppListForCurUser(
			WmsWorkerAppQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		WmsWorkerAppDaoQueryParam daoQueryParam = new WmsWorkerAppDaoQueryParam();

		if (StringUtils.hasText(queryParam.getKeyword())) {
			if (CheckUtils.isTrue(queryParam.getExactMatch())) {
				daoQueryParam.setKeyword(queryParam.getKeyword());
			} else {
				daoQueryParam.setKeyword("%" + queryParam.getKeyword() + "%");
			}
		}

		if (StringUtils.hasText(queryParam.getSortBy())) {
			String orderBy = queryParam.getSortBy();

			if (queryParam.getSortDesc() != null && queryParam.getSortDesc()) {
				orderBy += " desc";
			}

			daoQueryParam.setOrderByClause(orderBy);
		}

		PageHelper.startPage(pageNum, pageSize);

		List<WmsWorkerAppFull> workerAppFullList =
				workerAppDao.getWorkerAppListByUserId(
						userDetails.getUserId(), daoQueryParam);

		return CommonPage.restPage(workerAppFullList);
	}

	private Map<Long, WmsWorkerAppUserRelation> getWorkerAppUserRelationMapByUserId(Long userId) {
		Map<Long, WmsWorkerAppUserRelation> relationMap =
				workerAppCacheService.getWorkerAppUserRelationMapByUserId(userId);

		if (relationMap != null) {
			return relationMap;
		}

		WmsWorkerAppUserRelationExample example = new WmsWorkerAppUserRelationExample();
		WmsWorkerAppUserRelationExample.Criteria criteria = example.createCriteria();

		criteria.andUserIdEqualTo(userId);

		List<WmsWorkerAppUserRelation> relationList =
				workerAppUserRelationMapper.selectByExample(example);

		Map<Long, WmsWorkerAppUserRelation> relationMapNew = new HashMap<>();
		relationList.forEach(relation -> {
			relationMapNew.put(relation.getWorkerAppId(), relation);
		});

		workerAppCacheService.setWorkerAppUserRelationMapByUserId(userId, relationMapNew);

		return relationMapNew;
	}

	private WmsWorkerApp getWorkerAppByNameFromDb(String name) {
		WmsWorkerAppExample example = new WmsWorkerAppExample();
		example.createCriteria().andNameEqualTo(name);

		List<WmsWorkerApp> workerAppListList = workerAppMapper.selectByExample(example);

		if (workerAppListList != null && workerAppListList.size() > 0) {
			WmsWorkerApp workerApp = workerAppListList.get(0);

			workerAppCacheService.setWorkerApp(workerApp);

			return workerApp;
		}

		return null;
	}
}
