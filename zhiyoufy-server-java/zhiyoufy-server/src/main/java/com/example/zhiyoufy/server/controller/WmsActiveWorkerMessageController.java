package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import com.example.zhiyoufy.server.service.JmsJobRunService;
import com.example.zhiyoufy.server.service.WmsActiveWorkerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WmsActiveWorkerMessageController {
	private static final byte[] EMPTY_PAYLOAD = new byte[0];

	@Autowired
	private SimpMessagingTemplate clientMessagingTemplate;

	@Autowired
	private WmsActiveWorkerService activeWorkerService;

	@Autowired
	JmsJobRunService jobRunService;

	@MessageExceptionHandler
	public void handleException(SimpMessageHeaderAccessor headerAccessor,
			RuntimeException exception) {
		String sessionId = headerAccessor.getSessionId();

		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
		stompHeaderAccessor.setSessionId(sessionId);
		stompHeaderAccessor.setDestination("/error");
		stompHeaderAccessor.setMessage(exception.getMessage());

		Message<byte[]> message = MessageBuilder.createMessage(EMPTY_PAYLOAD,
				stompHeaderAccessor.getMessageHeaders());

		clientMessagingTemplate.send(message);
	}

	@MessageMapping("/worker-register")
	public void workerRegister(Message<WmsWorkerRegisterParam> message) {
		activeWorkerService.onWorkerRegister(message);
	}

	@MessageMapping("/start-job-child-run-rsp")
	public void startJobChildRunRsp(Message<JmsStartJobChildRunRsp> message) {
		jobRunService.onStartJobChildRunRsp(message);
	}

	@MessageMapping("/stop-job-child-run-rsp")
	public void stopJobChildRunRsp(Message<JmsStopJobChildRunRsp> message) {
		jobRunService.onStopJobChildRunRsp(message);
	}

	@MessageMapping("/job-child-run-result-ind")
	public void jobChildRunResultInd(Message<JmsJobChildRunResultInd> message) {
		jobRunService.onJobChildRunResultInd(message);
	}
}
