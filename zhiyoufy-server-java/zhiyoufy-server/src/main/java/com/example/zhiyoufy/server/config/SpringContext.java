package com.example.zhiyoufy.server.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.example.zhiyoufy.server.elasticsearch.JmsJobChildRunResultFullRepository;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import com.example.zhiyoufy.server.manager.finishedjobrun.FinishedJobRunResultStore;
import com.example.zhiyoufy.server.manager.scheduledjobrun.ScheduledJobRunManager;
import com.google.common.eventbus.EventBus;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SpringContext {
	private static SpringContext instance;

	public SpringContext() {
		instance = this;
	}

	public static SpringContext getInstance() {
		return instance;
	}

	@Autowired
	ZhiyoufyServerProperties zhiyoufyServerProperties;

	@Autowired
	EventBus eventBus;

	@Autowired
	ExecutorService zhiyoufyExecutorService;

	@Autowired
	ScheduledExecutorService zhiyoufyScheduledExecutorService;

	@Autowired
	SimpMessagingTemplate clientMessagingTemplate;

	@Autowired
	ActiveJobRunManager activeJobRunManager;

	@Autowired
	ActiveWorkerManager activeWorkerManager;

	@Autowired
	ScheduledJobRunManager scheduledJobRunManager;

	@Autowired
	JmsJobChildRunResultFullRepository jobChildRunResultFullRepository;

	@Autowired
	FinishedJobRunResultStore finishedJobRunResultStore;
}
