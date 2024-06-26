package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.EmsEnvironmentUserRelation;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigCollectionUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigSingleUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUpdateParam;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentUserRelationUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmsEnvironmentStructMapper {
	EmsEnvironmentStructMapper INSTANCE = Mappers.getMapper( EmsEnvironmentStructMapper.class );

	EmsEnvironment emsEnvironmentParamToEmsEnvironment(EmsEnvironmentParam environmentParam);
	EmsEnvironment emsEnvironmentUpdateParamToEmsEnvironment(EmsEnvironmentUpdateParam updateParam);
	EmsEnvironmentFull emsEnvironmentFullToEmsEnvironment(EmsEnvironment environment);

	EmsEnvironmentUserRelation relationFullToRelation(EmsEnvironmentUserRelationFull relationFull);
	EmsEnvironmentUserRelation updateParamToRelation(EmsEnvironmentUserRelationUpdateParam updateParam);

	EmsConfigSingle emsConfigSingleParamToEmsConfigSingle(EmsConfigSingleParam configSingleParam);
	EmsConfigSingle emsConfigSingleUpdateParamToEmsConfigSingle(EmsConfigSingleUpdateParam updateParam);

	EmsConfigCollection emsConfigCollectionParamToEmsConfigCollection(
			EmsConfigCollectionParam configCollectionParam);
	EmsConfigCollection emsConfigCollectionUpdateParamToEmsConfigCollection(
			EmsConfigCollectionUpdateParam updateParam);

	EmsConfigItem emsConfigItemParamToEmsConfigItem(EmsConfigItemParam configItemParam);
	EmsConfigItem emsConfigItemUpdateParamToEmsConfigItem(EmsConfigItemUpdateParam updateParam);
}
