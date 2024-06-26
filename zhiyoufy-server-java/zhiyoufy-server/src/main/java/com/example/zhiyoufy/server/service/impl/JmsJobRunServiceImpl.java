package com.example.zhiyoufy.server.service.impl;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.server.config.SpringContext;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobRun;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import com.example.zhiyoufy.server.manager.finishedjobrun.FinishedJobRunResultStore;
import com.example.zhiyoufy.server.mapstruct.JmsJobRunStructMapper;
import com.example.zhiyoufy.server.service.JmsJobRunService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.util.ElasticsearchUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Getter
@Setter
@Slf4j
public class JmsJobRunServiceImpl implements JmsJobRunService {
	@Autowired
	UmsUserService userService;

	@Autowired
	ActiveJobRunManager activeJobRunManager;

	@Autowired
	ElasticsearchOperations elasticsearchOperations;

	@Autowired
	FinishedJobRunResultStore finishedJobRunResultStore;

	@Override
	public JmsActiveJobRun startJobRun(JmsStartJobRunParam startJobRunParam) {
		UmsUserDetails userDetails = userService.getUserDetails();

		startJobRunParam.setUsername(userDetails.getUsername());

		return activeJobRunManager.startJobRun(startJobRunParam);
	}

	@Override
	public void stopJobRun(String runGuid) {
		activeJobRunManager.stopJobRun(runGuid);
	}

	@Override
	public void updatePerfParallelNum(JmsJobRunUpdatePerfParallelNumReq updateParallelNumReq) {
		activeJobRunManager.updatePerfParallelNum(updateParallelNumReq);
	}

	@Override
	public List<JmsActiveJobRunBase> getActiveJobRunBaseList(Boolean allUsers) {
		return activeJobRunManager.getActiveJobRunBaseList(allUsers);
	}

	@Override
	public JmsActiveJobRunBase getActiveJobRunBaseSingle(String runGuid) {
		return activeJobRunManager.getActiveJobRunBase(runGuid);
	}

	@Override
	public void onStartJobChildRunRsp(Message<JmsStartJobChildRunRsp> message) {
		JmsStartJobChildRunRsp startJobChildRunRsp = message.getPayload();

		activeJobRunManager.onStartJobChildRunRsp(startJobChildRunRsp);
	}

	@Override
	public void onStopJobChildRunRsp(Message<JmsStopJobChildRunRsp> message) {
		JmsStopJobChildRunRsp stopJobChildRunRsp = message.getPayload();

		activeJobRunManager.onStopJobChildRunRsp(stopJobChildRunRsp);
	}

	@Override
	public void onJobChildRunResultInd(Message<JmsJobChildRunResultInd> message) {
		JmsJobChildRunResultInd childRunResultInd = message.getPayload();

		try {
			activeJobRunManager.onJobChildRunResultInd(childRunResultInd);
		} catch (Throwable throwable) {
			log.error("onJobChildRunResultInd failed", throwable);

			MessageHeaders headers = message.getHeaders();
			String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

			SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			accessor.setSessionId(sessionId);
			accessor.setSubscriptionId("0");

			MessageHeaders headersToSend = accessor.getMessageHeaders();

			String destination = "/app/job-child-run-result-rsp";

			JmsJobChildRunResultRsp childRunResultRsp = new JmsJobChildRunResultRsp();
			childRunResultRsp.setMessageId(childRunResultInd.getMessageId());
			childRunResultRsp.setRunGuid(childRunResultInd.getRunGuid());
			childRunResultRsp.setIndex(childRunResultInd.getIndex());

			SimpMessagingTemplate clientMessagingTemplate =
					SpringContext.getInstance().getClientMessagingTemplate();
			clientMessagingTemplate.convertAndSend(destination, childRunResultRsp, headersToSend);
		}
	}

	@Override
	public CommonPage<JmsJobRunResultFull> getJobRunResultList(
			JmsJobRunResultQueryParam queryParam, Integer pageSize, Integer pageNum) {
		Criteria criteria = new Criteria("projectId").is(queryParam.getProjectId());

		if (queryParam.getEnvironmentId() != null) {
			Criteria envCriteria = new Criteria("environmentId").is(queryParam.getEnvironmentId());
			criteria = criteria.and(envCriteria);
		}

		if (StringUtils.hasText(queryParam.getTemplateName())) {
			Criteria templateCriteria = new Criteria("templateName")
					.is(queryParam.getTemplateName());
			criteria = criteria.and(templateCriteria);
		}

		if (StringUtils.hasText(queryParam.getRunTag())) {
			Criteria runTagCriteria = new Criteria("runTag")
					.is(queryParam.getRunTag());
			criteria = criteria.and(runTagCriteria);
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		Query query = new CriteriaQuery(criteria, pageable);
		SearchHits<JmsJobRunResultFull> searchHits =
				elasticsearchOperations.search(query, JmsJobRunResultFull.class);

		CommonPage<JmsJobRunResultFull> page = ElasticsearchUtils.searchHitsToCommonPage(
				searchHits, pageSize, pageNum);

		return page;
	}

	@Override
	public CommonPage<JmsJobChildRunResultFull> getJobChildRunResultList(
			JmsJobChildRunResultQueryParam queryParam, Integer pageSize, Integer pageNum) {
		Assert.hasText(queryParam.getRunGuid(), "runGuid is empty");

		Criteria criteria = new Criteria("projectId").is(queryParam.getProjectId());

		if (queryParam.getEnvironmentId() != null) {
			Criteria envCriteria = new Criteria("environmentId").is(queryParam.getEnvironmentId());
			criteria = criteria.and(envCriteria);
		}

		if (queryParam.getTemplateId() != null) {
			Criteria templateCriteria = new Criteria("templateId").is(queryParam.getTemplateId());
			criteria = criteria.and(templateCriteria);
		}

		Criteria runGuidCriteria = new Criteria("runGuid")
				.is(queryParam.getRunGuid());
		criteria = criteria.and(runGuidCriteria);

		Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		Query query = new CriteriaQuery(criteria, pageable);
		SearchHits<JmsJobChildRunResultFull> searchHits =
				elasticsearchOperations.search(query, JmsJobChildRunResultFull.class);

		CommonPage<JmsJobChildRunResultFull> page = ElasticsearchUtils.searchHitsToCommonPage(
				searchHits, pageSize, pageNum);

		return page;
	}

	@Override
	public JmsJobRunResultFull getJobRunResult(String runGuid) {
		JmsJobRunResultFull jmsJobRunResultFull;
		JmsActiveJobRunBase activeJobRunBase = activeJobRunManager.getActiveJobRunBase(runGuid);
		if(activeJobRunBase != null) {
			jmsJobRunResultFull = JmsJobRunStructMapper.INSTANCE
					.activeJobRunBaseToJobRunResultFull(activeJobRunBase);

		} else {
			jmsJobRunResultFull = finishedJobRunResultStore.getFinishedJobRunResult(runGuid);
		}

		if(jmsJobRunResultFull == null) {
			//get job run result from elk
			Criteria runGuidCriteria = new Criteria("runGuid")
					.is(runGuid);

			Query query = new CriteriaQuery(runGuidCriteria);

			SearchHits<JmsJobRunResultFull> searchHits =
					elasticsearchOperations.search(query, JmsJobRunResultFull.class);

			if(searchHits.getTotalHits() > 0) {
				SearchHit<JmsJobRunResultFull> searchHit = searchHits.getSearchHit(0);
				jmsJobRunResultFull = searchHit.getContent();
			}

		}

		return jmsJobRunResultFull;

	}
}
