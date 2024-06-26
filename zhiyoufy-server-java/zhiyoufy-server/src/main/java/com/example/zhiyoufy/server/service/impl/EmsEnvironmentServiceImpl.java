package com.example.zhiyoufy.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.EmsEnvironmentMapper;
import com.example.zhiyoufy.mbg.mapper.EmsEnvironmentUserRelationMapper;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentExample;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelationExample;
import com.example.zhiyoufy.server.dao.EmsEnvironmentDao;
import com.example.zhiyoufy.server.dao.EmsEnvironmentUserRelationDao;
import com.example.zhiyoufy.server.domain.bo.ems.EmsEnvironmentDaoQueryParam;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentBase;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationUpdateParam;
import com.example.zhiyoufy.server.mapstruct.EmsEnvironmentStructMapper;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import com.example.zhiyoufy.server.service.EmsConfigSingleService;
import com.example.zhiyoufy.server.service.EmsEnvironmentCacheService;
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
public class EmsEnvironmentServiceImpl implements EmsEnvironmentService {
	@Autowired
	EmsEnvironmentMapper environmentMapper;
	@Autowired
	EmsEnvironmentUserRelationMapper environmentUserRelationMapper;

	@Autowired
	EmsEnvironmentDao environmentDao;
	@Autowired
	EmsEnvironmentUserRelationDao environmentUserRelationDao;

	@Autowired
	EmsConfigCollectionService configCollectionService;
	@Autowired
	EmsConfigSingleService configSingleService;
	@Autowired
	EmsEnvironmentCacheService environmentCacheService;
	@Autowired
	UmsUserService userService;

	@Override
	@PreAuthorize("hasAuthority('environments.create')")
	@Transactional
	public EmsEnvironment addEnvironment(EmsEnvironmentParam environmentParam) {
		Assert.notNull(environmentParam, "environmentParam is null");
		Assert.hasText(environmentParam.getName(), "name is empty");

		EmsEnvironment emsEnvironment = getEnvironmentByName(environmentParam.getName());

		if (emsEnvironment != null) {
			Asserts.fail(CommonErrorCode.RES_NAME_ALREADY_EXIST);
		}

		if (environmentParam.getParentId() != null) {
			Assert.hasText(environmentParam.getParentName(), "parentId set, but parentName is empty");

			EmsEnvironment parentEnv = getEnvironmentById(environmentParam.getParentId());

			if (parentEnv == null || !environmentParam.getParentName().equals(parentEnv.getName())) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
			}
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		// add EmsEnvironment
		emsEnvironment = EmsEnvironmentStructMapper.INSTANCE
				.emsEnvironmentParamToEmsEnvironment(environmentParam);
		emsEnvironment.setCreatedBy(userDetails.getUsername());
		emsEnvironment.setModifiedBy(userDetails.getUsername());

		environmentMapper.insertSelective(emsEnvironment);

		// add EmsEnvironmentUserRelation
		EmsEnvironmentUserRelation environmentUserRelation = new EmsEnvironmentUserRelation();
		environmentUserRelation.setEnvironmentId(emsEnvironment.getId());
		environmentUserRelation.setUserId(userDetails.getUserId());
		environmentUserRelation.setIsOwner(true);

		environmentUserRelationMapper.insertSelective(environmentUserRelation);

		// select to include DB generated time values
		emsEnvironment = environmentMapper.selectByPrimaryKey(emsEnvironment.getId());

		environmentCacheService.clearAll();

		return emsEnvironment;
	}

	@Override
	@PreAuthorize("hasAuthority('environments.delete')")
	@Transactional
	public Integer delEnvironmentById(Long envId) {
		EmsEnvironment emsEnvironment = getEnvironmentById(envId);

		if (emsEnvironment == null) {
			return 0;
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation environmentUserRelation =
					getEnvironmentUserRelationByEnvIdAndUserId(envId, userDetails.getUserId());

			if (environmentUserRelation == null || !environmentUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		// delete EmsConfigCollection
		{
			configCollectionService.delConfigCollectionsByEnvId(envId);
		}

		// delete EmsConfigSingle
		{
			configSingleService.delConfigSinglesByEnvId(envId);
		}

		// delete EmsEnvironmentUserRelation
		{
			EmsEnvironmentUserRelationExample example = new EmsEnvironmentUserRelationExample();
			EmsEnvironmentUserRelationExample.Criteria criteria = example
					.createCriteria();

			criteria.andEnvironmentIdEqualTo(envId);

			environmentUserRelationMapper.deleteByExample(example);
		}

		// update parentId
		{
			EmsEnvironmentExample example = new EmsEnvironmentExample();
			EmsEnvironmentExample.Criteria criteria = example.createCriteria();

			criteria.andParentIdEqualTo(envId);

			EmsEnvironment environmentUpdate = new EmsEnvironment();
			environmentUpdate.setParentId(0L);

			environmentMapper.updateByExampleSelective(environmentUpdate, example);
		}

		// delete EmsEnvironment
		int deleted = environmentMapper.deleteByPrimaryKey(envId);

		environmentCacheService.clearAll();

		return deleted;
	}

	@Override
	public EmsEnvironment getEnvironmentById(Long envId) {
		EmsEnvironment environment = environmentCacheService.getEnvironmentById(envId);

		if (environment != null) {
			return environment;
		}

		environment = environmentMapper.selectByPrimaryKey(envId);

		if (environment != null) {
			environmentCacheService.setEnvironment(environment);
		}

		return environment;
	}

	@Override
	public EmsEnvironment getEnvironmentByName(String name) {
		EmsEnvironment emsEnvironment = getEnvironmentByNameFromDb(name);

		return emsEnvironment;
	}

	@Override
	@PreAuthorize("hasAuthority('environments.get')")
	public CommonPage<EmsEnvironmentFull> getEnvironmentList(EmsEnvironmentQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		if (queryParam.getAllUsers() == null || !queryParam.getAllUsers()) {
			return getEnvironmentListForCurUser(queryParam, pageSize, pageNum);
		}

		return getEnvironmentListForAllUsers(queryParam, pageSize, pageNum);
	}

	@Override
	public List<EmsEnvironmentBase> getEnvironmentBaseList() {
		UmsUserDetails userDetails = userService.getUserDetails();

		List<EmsEnvironmentBase> environmentBaseList =
				environmentDao.getEnvironmentBaseListByUserId(userDetails.getUserId());

		return environmentBaseList;
	}

	@Override
	@PreAuthorize("hasAuthority('environments.update')")
	public int updateEnvironment(Long id, EmsEnvironmentUpdateParam updateParam) {
		Assert.notNull(updateParam, "updateParam is null");

		EmsEnvironment targetEnvironment = getEnvironmentById(id);

		if (targetEnvironment == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation environmentUserRelation =
					getEnvironmentUserRelationByEnvIdAndUserId(id, userDetails.getUserId());

			if (environmentUserRelation == null || (!environmentUserRelation.getIsOwner() &&
					!environmentUserRelation.getIsEditor())) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsEnvironment emsEnvironment = EmsEnvironmentStructMapper.INSTANCE
				.emsEnvironmentUpdateParamToEmsEnvironment(updateParam);
		emsEnvironment.setId(id);

		environmentCacheService.clearAll();

		return environmentMapper.updateByPrimaryKeySelective(emsEnvironment);
	}

	@Override
	public EmsEnvironmentUserRelation getEnvironmentUserRelationByEnvIdAndUserId(Long envId, Long userId) {
		Map<Long, EmsEnvironmentUserRelation> relationMap = getEnvironmentUserRelationMapByUserId(userId);

		return relationMap.get(envId);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner')")
	public int addEnvironmentUserRelation(
			EmsEnvironmentUserRelationFull relationFull) {
		Assert.isNull(relationFull.getId(), "id should be null");

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation curUserRelation =
					getEnvironmentUserRelationByEnvIdAndUserId(
							relationFull.getEnvironmentId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsEnvironmentUserRelation relation = EmsEnvironmentStructMapper.INSTANCE
				.relationFullToRelation(relationFull);
		int cnt = environmentUserRelationMapper.insert(relation);

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner')")
	@Transactional
	public int delEnvironmentUserRelationById(Long id) {
		EmsEnvironmentUserRelation relation =
				environmentUserRelationMapper.selectByPrimaryKey(id);

		if (relation == null) {
			Asserts.fail(CommonErrorCode.RES_NOT_FOUND);
		}

		if (relation.getIsOwner()) {
			EmsEnvironmentUserRelationExample example =
					new EmsEnvironmentUserRelationExample();
			EmsEnvironmentUserRelationExample.Criteria criteria = example.createCriteria();

			criteria.andEnvironmentIdEqualTo(relation.getEnvironmentId())
					.andIsOwnerEqualTo(true);

			long ownerCnt = environmentUserRelationMapper.countByExample(example);

			if (ownerCnt == 1) {
				Asserts.fail(CommonErrorCode.RES_DELETE_LAST_OWNER_NOT_ALLOWED);
			}
		}

		int cnt = environmentUserRelationMapper.deleteByPrimaryKey(id);

		return cnt;
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner')")
	public CommonPage<EmsEnvironmentUserRelationFull> getEnvironmentUserRelationListByEnvId(
			Long envId, Integer pageSize, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);

		List<EmsEnvironmentUserRelationFull> relationFullList =
				environmentUserRelationDao.getUserRelationListByEnvId(envId);

		return CommonPage.restPage(relationFullList);
	}

	@Override
	@PreAuthorize("hasAuthority('roles/environment.owner')")
	public int updateEnvironmentUserRelation(Long id,
			EmsEnvironmentUserRelationUpdateParam updateParam) {
		EmsEnvironmentUserRelation targetRelation = environmentUserRelationMapper.selectByPrimaryKey(id);

		if (targetRelation == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			EmsEnvironmentUserRelation curUserRelation =
					getEnvironmentUserRelationByEnvIdAndUserId(
							targetRelation.getEnvironmentId(), userDetails.getUserId());

			if (curUserRelation == null || !curUserRelation.getIsOwner()) {
				Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
			}
		}

		EmsEnvironmentUserRelation relation = EmsEnvironmentStructMapper.INSTANCE
				.updateParamToRelation(updateParam);
		relation.setId(id);

		return environmentUserRelationMapper.updateByPrimaryKeySelective(relation);
	}

	private CommonPage<EmsEnvironmentFull> getEnvironmentListForAllUsers(
			EmsEnvironmentQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		if (!userDetails.isAdmin()) {
			Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
		}

		EmsEnvironmentExample example = new EmsEnvironmentExample();
		EmsEnvironmentExample.Criteria criteria = example.createCriteria();

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

		List<EmsEnvironment> environmentList =
				environmentMapper.selectByExample(example);
		List<EmsEnvironmentFull> environmentFullList = environmentList.stream()
				.map(environment -> {
					EmsEnvironmentFull environmentFull = EmsEnvironmentStructMapper.INSTANCE
							.emsEnvironmentFullToEmsEnvironment(environment);
					return environmentFull;
				})
				.collect(Collectors.toList());

		return CommonPage.restPage(environmentFullList, environmentList);
	}

	private CommonPage<EmsEnvironmentFull> getEnvironmentListForCurUser(
			EmsEnvironmentQueryParam queryParam,
			Integer pageSize, Integer pageNum) {
		UmsUserDetails userDetails = userService.getUserDetails();

		EmsEnvironmentDaoQueryParam daoQueryParam = new EmsEnvironmentDaoQueryParam();

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

		List<EmsEnvironmentFull> environmentFullList =
				environmentDao.getEnvironmentListByUserId(
						userDetails.getUserId(), daoQueryParam);

		return CommonPage.restPage(environmentFullList);
	}

	private Map<Long, EmsEnvironmentUserRelation> getEnvironmentUserRelationMapByUserId(Long userId) {
		Map<Long, EmsEnvironmentUserRelation> relationMap =
				environmentCacheService.getEnvironmentUserRelationMapByUserId(userId);

		if (relationMap != null) {
			return relationMap;
		}

		EmsEnvironmentUserRelationExample example = new EmsEnvironmentUserRelationExample();
		EmsEnvironmentUserRelationExample.Criteria criteria = example.createCriteria();

		criteria.andUserIdEqualTo(userId);

		List<EmsEnvironmentUserRelation> relationList =
				environmentUserRelationMapper.selectByExample(example);

		Map<Long, EmsEnvironmentUserRelation> relationMapNew = new HashMap<>();
		relationList.forEach(relation -> {
			relationMapNew.put(relation.getEnvironmentId(), relation);
		});

		environmentCacheService.setEnvironmentUserRelationMapByUserId(userId, relationMapNew);

		return relationMapNew;
	}

	private EmsEnvironment getEnvironmentByNameFromDb(String name) {
		EmsEnvironmentExample example = new EmsEnvironmentExample();
		example.createCriteria().andNameEqualTo(name);

		List<EmsEnvironment> environmentListList = environmentMapper.selectByExample(example);

		if (environmentListList != null && environmentListList.size() > 0) {
			EmsEnvironment environment = environmentListList.get(0);

			environmentCacheService.setEnvironment(environment);

			return environment;
		}

		return null;
	}
}
