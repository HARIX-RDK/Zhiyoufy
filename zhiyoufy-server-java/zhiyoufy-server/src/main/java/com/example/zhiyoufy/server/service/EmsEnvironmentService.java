package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentBase;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentQueryParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationUpdateParam;

public interface EmsEnvironmentService {
	EmsEnvironment addEnvironment(EmsEnvironmentParam environmentParam);

	Integer delEnvironmentById(Long envId);

	EmsEnvironment getEnvironmentById(Long envId);

	EmsEnvironment getEnvironmentByName(String name);

	CommonPage<EmsEnvironmentFull> getEnvironmentList(EmsEnvironmentQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	List<EmsEnvironmentBase> getEnvironmentBaseList();

	int updateEnvironment(Long id, EmsEnvironmentUpdateParam updateParam);

	EmsEnvironmentUserRelation getEnvironmentUserRelationByEnvIdAndUserId(
			Long envId, Long userId);

	int addEnvironmentUserRelation(EmsEnvironmentUserRelationFull relationFull);
	int delEnvironmentUserRelationById(Long id);
	CommonPage<EmsEnvironmentUserRelationFull> getEnvironmentUserRelationListByEnvId(
			Long envId, Integer pageSize, Integer pageNum);
	int updateEnvironmentUserRelation(Long id,
			EmsEnvironmentUserRelationUpdateParam updateParam);
}
