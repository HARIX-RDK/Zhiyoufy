package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JmsJobRunStructMapper {
	JmsJobRunStructMapper INSTANCE = Mappers.getMapper(JmsJobRunStructMapper.class);

	JmsActiveJobRunBase startJobRunParamToActiveJobRunBase(JmsStartJobRunParam startJobRunParam);

	JmsJobChildRunResultFull activeJobRunBaseToJobChildRunResultFull(JmsActiveJobRunBase activeJobRunBase);
	void updateJobChildRunResultFullFromJobChildRunResultInd(JmsJobChildRunResultInd childRunResultInd,
			@MappingTarget JmsJobChildRunResultFull jobChildRunResultFull);

	JmsJobRunResultFull activeJobRunBaseToJobRunResultFull(JmsActiveJobRunBase activeJobRunBase);
}
