package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobRun;

import org.springframework.messaging.Message;

public interface JmsJobRunService {
	JmsActiveJobRun startJobRun(JmsStartJobRunParam startJobRunParam);
	void stopJobRun(String runGuid);
	void updatePerfParallelNum(JmsJobRunUpdatePerfParallelNumReq updateParallelNumReq);
	List<JmsActiveJobRunBase> getActiveJobRunBaseList(Boolean allUsers);
	JmsActiveJobRunBase getActiveJobRunBaseSingle(String runGuid);

	void onStartJobChildRunRsp(Message<JmsStartJobChildRunRsp> message);
	void onStopJobChildRunRsp(Message<JmsStopJobChildRunRsp> message);
	void onJobChildRunResultInd(Message<JmsJobChildRunResultInd> message);

	CommonPage<JmsJobRunResultFull> getJobRunResultList(JmsJobRunResultQueryParam queryParam,
			Integer pageSize, Integer pageNum);
	CommonPage<JmsJobChildRunResultFull> getJobChildRunResultList(JmsJobChildRunResultQueryParam queryParam,
			Integer pageSize, Integer pageNum);

	JmsJobRunResultFull getJobRunResult(String runGuid);
}
