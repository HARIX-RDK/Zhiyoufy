package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerAppUserRelation;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsGroupTokenUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerGroupUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppUserRelationUpdateParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WmsWorkerAppStructMapper {
	WmsWorkerAppStructMapper INSTANCE = Mappers.getMapper(WmsWorkerAppStructMapper.class);

	WmsWorkerApp wmsWorkerAppParamToWmsWorkerApp(WmsWorkerAppParam workerAppParam);
	WmsWorkerApp wmsWorkerAppUpdateParamToWmsWorkerApp(WmsWorkerAppUpdateParam updateParam);
	WmsWorkerAppFull wmsWorkerAppToWmsWorkerAppFull(WmsWorkerApp workerApp);

	WmsWorkerAppUserRelation relationFullToRelation(WmsWorkerAppUserRelationFull relationFull);
	WmsWorkerAppUserRelation updateParamToRelation(WmsWorkerAppUserRelationUpdateParam updateParam);

	WmsWorkerGroup wmsWorkerGroupParamToWmsWorkerGroup(
			WmsWorkerGroupParam workerGroupParam);
	WmsWorkerGroup wmsWorkerGroupUpdateParamToWmsWorkerGroup(
			WmsWorkerGroupUpdateParam updateParam);

	WmsGroupToken wmsGroupTokenParamToWmsGroupToken(
			WmsGroupTokenParam workerGroupParam);
	WmsGroupToken wmsGroupTokenUpdateParamToWmsGroupToken(
			WmsGroupTokenUpdateParam updateParam);

	WmsActiveWorkerBase registerParamToWmsActiveWorkerBase(
			WmsWorkerRegisterParam registerParam);
}
