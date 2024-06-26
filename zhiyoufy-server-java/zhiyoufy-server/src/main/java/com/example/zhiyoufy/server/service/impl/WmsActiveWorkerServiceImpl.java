package com.example.zhiyoufy.server.service.impl;

import java.util.Date;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.ErrorInfo;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.WmsGroupToken;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterResult;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.mapstruct.WmsWorkerAppStructMapper;
import com.example.zhiyoufy.server.service.WmsActiveWorkerService;
import com.example.zhiyoufy.server.service.WmsGroupTokenService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@Slf4j
public class WmsActiveWorkerServiceImpl implements WmsActiveWorkerService {
	@Autowired
	WmsWorkerAppService workerAppService;
	@Autowired
	WmsWorkerGroupService workerGroupService;
	@Autowired
	WmsGroupTokenService groupTokenService;

	@Autowired
	private SimpMessagingTemplate clientMessagingTemplate;

	@Autowired
	private ActiveWorkerManager activeWorkerManager;

	@Override
	public void onWorkerRegister(Message<WmsWorkerRegisterParam> message) {
		String logPrefix = "onWorkerRegister:";

		WmsWorkerRegisterParam registerParam = message.getPayload();
		log.debug("{} Got a message: {}", logPrefix, StrUtils.jsonDump(registerParam));

		String destination = "/app/worker-register-rsp";
		WmsWorkerRegisterResult registerResult = new WmsWorkerRegisterResult();
		registerResult.setServerRunId(activeWorkerManager.getServerRunId());

		WmsWorkerApp workerApp = workerAppService.getWorkerAppByName(
				registerParam.getWorkerApp());

		if (workerApp == null) {
			registerResult.setError(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT,
					"Not found corresponding workerApp"));
			sendResponse(message, destination, registerResult);
			return;
		}

		WmsWorkerGroup workerGroup =
				workerGroupService.getWorkerGroupByWorkerAppIdAndName(
						workerApp.getId(), registerParam.getWorkerGroup());

		if (workerGroup == null) {
			registerResult.setError(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT,
					"Not found corresponding workerGroup"));
			sendResponse(message, destination, registerResult);
			return;
		}

		WmsGroupToken groupToken =
				groupTokenService.getGroupTokenByWorkerGroupIdAndName(
						workerGroup.getId(), registerParam.getGroupTokenName());

		if (groupToken == null) {
			registerResult.setError(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT,
					"Not found corresponding groupToken"));
			sendResponse(message, destination, registerResult);
			return;
		}

		if (!groupToken.getSecret().equals(registerParam.getGroupTokenSecret())) {
			registerResult.setError(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_ARGUMENT,
					"Invalid groupTokenSecret"));
			sendResponse(message, destination, registerResult);
			return;
		}

		MessageHeaders headers = message.getHeaders();
		String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

		WmsActiveWorkerBase activeWorkerBase = WmsWorkerAppStructMapper.INSTANCE
				.registerParamToWmsActiveWorkerBase(registerParam);
		activeWorkerBase.setSessionId(sessionId);
		activeWorkerBase.setConnectTime(new Date());
		activeWorkerBase.setFreeActiveJobNum(activeWorkerBase.getMaxActiveJobNum());

		WmsActiveWorker activeWorker = new WmsActiveWorker();
		activeWorker.setActiveWorkerBase(activeWorkerBase);

		activeWorker.setWorkerApp(workerApp);
		activeWorker.setWorkerGroup(workerGroup);
		activeWorker.setRegisterParam(registerParam);

		try {
			activeWorkerManager.onActiveWorkerRegister(activeWorker);
		} catch (Throwable error) {
			registerResult.setError(ErrorInfo.of(CommonErrorCode.RES_ILLEGAL_STATE,
					error.getMessage()));
			sendResponse(message, destination, registerResult);
			return;
		}

		sendResponse(message, destination, registerResult);

		log.debug("{} Leave", logPrefix);
	}

	@Override
	public void disconnectSession(String sessionId) {
		activeWorkerManager.disconnectSession(sessionId);
	}

	@Override
	public List<WmsWorkerAppBase> getAppBaseList() {
		return activeWorkerManager.getAppBaseList();
	}

	@Override
	public List<WmsActiveWorkerGroupBase> getGroupBaseListByAppId(Long appId) {
		return activeWorkerManager.getGroupBaseListByAppId(appId);
	}

	@Override
	public List<WmsActiveWorkerBase> getWorkerBaseListByGroupId(Long groupId) {
		return activeWorkerManager.getWorkerBaseListByGroupId(groupId);
	}

	private void sendResponse(Message<?> message, String destination, Object rsp) {
		MessageHeaders headers = message.getHeaders();
		String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setSessionId(sessionId);
		accessor.setSubscriptionId("0");

		MessageHeaders headersToSend = accessor.getMessageHeaders();

		clientMessagingTemplate.convertAndSend(destination, rsp, headersToSend);
	}
}
