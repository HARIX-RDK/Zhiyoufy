package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.PmsProject;
import com.example.zhiyoufy.mbg.model.PmsProjectUserRelation;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobFolderUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsJobTemplateUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUpdateParam;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationFull;
import com.example.zhiyoufy.server.domain.dto.pms.PmsProjectUserRelationUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PmsProjectStructMapper {
	PmsProjectStructMapper INSTANCE = Mappers.getMapper( PmsProjectStructMapper.class );

	PmsProject pmsProjectParamToPmsProject(PmsProjectParam projectParam);
	PmsProject pmsProjectUpdateParamToPmsProject(PmsProjectUpdateParam updateParam);
	PmsProjectFull pmsProjectToPmsProjectFull(PmsProject project);

	PmsProjectUserRelation relationFullToRelation(PmsProjectUserRelationFull relationFull);
	PmsProjectUserRelation updateParamToRelation(PmsProjectUserRelationUpdateParam updateParam);

	PmsJobFolder pmsJobFolderParamToPmsJobFolder(PmsJobFolderParam jobFolderParam);
	PmsJobFolder pmsJobFolderUpdateParamToPmsJobFolder(PmsJobFolderUpdateParam updateParam);

	PmsJobTemplate pmsJobTemplateParamToPmsJobTemplate(PmsJobTemplateParam jobTemplateParam);
	PmsJobTemplate pmsJobTemplateUpdateParamToPmsJobTemplate(PmsJobTemplateUpdateParam updateParam);
}
