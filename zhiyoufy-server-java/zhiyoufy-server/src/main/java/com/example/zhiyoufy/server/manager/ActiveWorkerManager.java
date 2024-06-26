package com.example.zhiyoufy.server.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.config.ZhiyoufyServerProperties;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveApp;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveGroup;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceReq;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceRsp;
import com.example.zhiyoufy.server.domain.bo.wms.WmsFreeJobResourceReq;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import com.example.zhiyoufy.server.domain.event.ActiveWorkerSessionTimeoutEvent;
import com.example.zhiyoufy.server.domain.event.WebSocketConnectionClosedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class ActiveWorkerManager {
	@Autowired
	ScheduledExecutorService zhiyoufyScheduledExecutorService;
	@Autowired
	ZhiyoufyServerProperties zhiyoufyServerProperties;
	@Autowired
	private SimpMessagingTemplate clientMessagingTemplate;

	private static final byte[] EMPTY_PAYLOAD = new byte[0];

	private EventBus eventBus;
	private String serverRunId;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readlock = readWriteLock.readLock();
	private final Lock writelock = readWriteLock.writeLock();

	private List<WmsWorkerAppBase> appBaseList = new CopyOnWriteArrayList<>();
	private Map<Long, List<WmsActiveWorkerGroupBase>> appGroupListMap = new ConcurrentHashMap<>();
	private Map<Long, List<WmsActiveWorkerBase>> groupWorkerListMap = new ConcurrentHashMap<>();

	private Map<Long, WmsActiveApp> activeAppMap = new HashMap<>();
	private Map<String, WmsActiveWorker> sessionToWorkerMap = new HashMap<>();
	private Map<String, WmsActiveWorker> workerKeyToWorkerMap = new HashMap<>();

	public ActiveWorkerManager(EventBus eventBus) {
		this.eventBus = eventBus;
		serverRunId = RandomUtils.generateShortHexId();

		eventBus.register(this);
	}

	public void onActiveWorkerRegister(WmsActiveWorker activeWorker) {
		String logPrefix = "onActiveWorkerRegister:";

		WmsActiveWorkerBase activeWorkerBase = activeWorker.getActiveWorkerBase();

		log.debug("{} Enter sessionId {}, app {}, group {}, worker {}, maxActiveJobNum {}",
				logPrefix, activeWorkerBase.getSessionId(),
				activeWorker.getWorkerApp().getName(),
				activeWorker.getWorkerGroup().getName(),
				activeWorker.getRegisterParam().getWorkerName(),
				activeWorker.getRegisterParam().getMaxActiveJobNum());

		writelock.lock();
		try {
			String sessionId = activeWorkerBase.getSessionId();

			if (sessionToWorkerMap.containsKey(sessionId)) {
				throw new RuntimeException("session already has worker");
			}

			if (workerKeyToWorkerMap.containsKey(activeWorker.getWorkerKey())) {
				WmsActiveWorker existActiveWorker =
						workerKeyToWorkerMap.get(activeWorker.getWorkerKey());
				WmsActiveWorkerBase existActiveWorkerBase =
						existActiveWorker.getActiveWorkerBase();

				if (!existActiveWorkerBase.isDisconnected()) {
					throw new RuntimeException("workerName already in use");
				}

				if (existActiveWorker.getSessionTimeoutFuture() != null) {
					existActiveWorker.getSessionTimeoutFuture().cancel(false);
					existActiveWorker.setSessionTimeoutFuture(null);
				}

				if (!existActiveWorker.getRegisterParam().getAppRunId().equals(
						activeWorker.getRegisterParam().getAppRunId())) {
					removeActiveWorker(existActiveWorker);

					addNewActiveWorker(activeWorker);
				} else {
					existActiveWorkerBase.setSessionId(sessionId);
					existActiveWorkerBase.setDisconnected(false);
					existActiveWorkerBase.setConnectTime(new Date());

					existActiveWorker.getActiveGroup().updateResourceInfo();

					activeWorker = existActiveWorker;
				}
			} else {
				addNewActiveWorker(activeWorker);
			}

			sessionToWorkerMap.put(sessionId, activeWorker);
		} finally {
			writelock.unlock();
		}
	}

	public WmsAllocJobResourceRsp allocJobResource(WmsAllocJobResourceReq req) {
		writelock.lock();
		try {
			WmsActiveGroup activeGroup = getActiveGroup(req.getAppId(), req.getGroupId());

			if (activeGroup == null) {
				return null;
			}

			return activeGroup.allocJobResource(req);
		} finally {
			writelock.unlock();
		}
	}

	public void freeJobResource(WmsFreeJobResourceReq req) {
		writelock.lock();
		try {
			WmsActiveGroup activeGroup = getActiveGroup(req.getAppId(), req.getGroupId());

			if (activeGroup == null) {
				return;
			}

			activeGroup.freeJobResource(req);
		} finally {
			writelock.unlock();
		}
	}

	public WmsActiveGroup getActiveGroup(Long appId, Long groupId) {
		readlock.lock();
		try {
			WmsActiveApp activeApp = activeAppMap.get(appId);

			if (activeApp == null) {
				return null;
			}

			WmsActiveGroup activeGroup = activeApp.getActiveGroupMap().get(groupId);

			return activeGroup;
		} finally {
			readlock.unlock();
		}
	}

	public void onSessionDisconnect(String sessionId) {
		if (!sessionToWorkerMap.containsKey(sessionId)) {
			return;
		}

		writelock.lock();
		try {
			WmsActiveWorker activeWorker = sessionToWorkerMap.remove(sessionId);

			if (activeWorker == null) {
				return;
			}

			WmsActiveWorkerBase activeWorkerBase = activeWorker.getActiveWorkerBase();

			activeWorkerBase.setDisconnected(true);
			activeWorkerBase.setDisconnectTime(new Date());

			activeWorker.getActiveGroup().updateResourceInfo();

			String logPrefix = "onSessionDisconnect:";

			log.debug("{} sessionId {}, app {}, group {}, worker {}, maxActiveJobNum {}",
					logPrefix, sessionId,
					activeWorker.getWorkerApp().getName(),
					activeWorker.getWorkerGroup().getName(),
					activeWorker.getRegisterParam().getWorkerName(),
					activeWorker.getRegisterParam().getMaxActiveJobNum());

			ScheduledFuture<?> sessionTimeoutFuture =
					zhiyoufyScheduledExecutorService.schedule(
							() -> onSessionTimeout(activeWorker),
							zhiyoufyServerProperties.getActiveWorkerSessionTimeout(),
							TimeUnit.SECONDS);
			activeWorker.setSessionTimeoutFuture(sessionTimeoutFuture);
		} finally {
			writelock.unlock();
		}
	}

	public void disconnectSession(String sessionId) {
		if (!sessionToWorkerMap.containsKey(sessionId)) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
		}

		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
		stompHeaderAccessor.setSessionId(sessionId);
		stompHeaderAccessor.setDestination("/error");
		stompHeaderAccessor.setMessage("disconnectSession requested");

		Message<byte[]> message = MessageBuilder.createMessage(EMPTY_PAYLOAD,
				stompHeaderAccessor.getMessageHeaders());

		clientMessagingTemplate.send(message);
	}

	public List<WmsWorkerAppBase> getAppBaseList() {
		return appBaseList;
	}

	public List<WmsActiveWorkerGroupBase> getGroupBaseListByAppId(Long appId) {
		return appGroupListMap.get(appId);
	}

	public List<WmsActiveWorkerBase> getWorkerBaseListByGroupId(Long groupId) {
		return groupWorkerListMap.get(groupId);
	}

	@Subscribe
	public void onWebSocketConnectionClosedEvent(WebSocketConnectionClosedEvent event) {
		onSessionDisconnect(event.getSessionId());
	}

	private void addNewActiveWorker(WmsActiveWorker activeWorker) {
		Long appId = activeWorker.getWorkerApp().getId();
		Long groupId = activeWorker.getWorkerGroup().getId();
		String workerName = activeWorker.getRegisterParam().getWorkerName();

		WmsActiveApp activeApp;

		if (!activeAppMap.containsKey(appId)) {
			WmsWorkerApp workerApp = activeWorker.getWorkerApp();

			WmsWorkerAppBase appBase = new WmsWorkerAppBase();
			appBase.setId(workerApp.getId());
			appBase.setName(workerApp.getName());

			appBaseList.add(appBase);
			appGroupListMap.put(appId, new CopyOnWriteArrayList<>());
			activeAppMap.put(appId, new WmsActiveApp(workerApp));
		}

		activeApp = activeAppMap.get(appId);

		WmsActiveGroup activeGroup;

		if (!activeApp.getActiveGroupMap().containsKey(groupId)) {
			WmsWorkerGroup workerGroup = activeWorker.getWorkerGroup();

			WmsActiveWorkerGroupBase groupBase = new WmsActiveWorkerGroupBase();
			groupBase.setId(groupId);
			groupBase.setWorkerAppId(appId);
			groupBase.setName(workerGroup.getName());
			groupBase.setWorkerLabels(workerGroup.getWorkerLabels());

			activeGroup = new WmsActiveGroup(activeWorker.getWorkerGroup());
			activeGroup.setWorkerGroupBase(groupBase);

			appGroupListMap.get(appId).add(groupBase);
			groupWorkerListMap.put(groupId, new CopyOnWriteArrayList<>());
			activeApp.getActiveGroupMap().put(groupId, activeGroup);
		}

		activeGroup = activeApp.getActiveGroupMap().get(groupId);

		Map<String, WmsActiveWorker> activeWorkerMap = activeGroup.getActiveWorkerMap();

		WmsActiveWorkerBase activeWorkerBase = activeWorker.getActiveWorkerBase();

		groupWorkerListMap.get(groupId).add(activeWorkerBase);
		activeWorkerMap.put(workerName, activeWorker);
		activeWorker.setActiveGroup(activeGroup);

		workerKeyToWorkerMap.put(activeWorker.getWorkerKey(), activeWorker);

		activeGroup.updateResourceInfo();
	}

	private void onSessionTimeout(WmsActiveWorker activeWorker) {
		boolean removed = false;

		writelock.lock();
		try {
			if (activeWorker.getActiveWorkerBase().isDisconnected()) {
				removeActiveWorker(activeWorker);
				removed = true;
			}
		} finally {
			writelock.unlock();
		}

		if (removed) {
			ActiveWorkerSessionTimeoutEvent event = new ActiveWorkerSessionTimeoutEvent();
			event.setActiveWorkerBase(activeWorker.getActiveWorkerBase());

			eventBus.post(event);
		}
	}

	private void removeActiveWorker(WmsActiveWorker activeWorker) {
		workerKeyToWorkerMap.remove(activeWorker.getWorkerKey());

		Long appId = activeWorker.getWorkerApp().getId();
		Long groupId = activeWorker.getWorkerGroup().getId();
		String workerName = activeWorker.getRegisterParam().getWorkerName();

		WmsActiveApp activeApp = activeAppMap.get(appId);
		WmsActiveGroup activeGroup = activeApp.getActiveGroupMap().get(groupId);
		Map<String, WmsActiveWorker> activeWorkerMap = activeGroup.getActiveWorkerMap();

		activeWorkerMap.remove(workerName);

		groupWorkerListMap.get(groupId).removeIf(
				activeWorkerBase -> activeWorkerBase.getWorkerName().equals(workerName));

		if (activeWorkerMap.isEmpty()) {
			activeApp.getActiveGroupMap().remove(groupId);
			groupWorkerListMap.remove(groupId);
			appGroupListMap.get(appId).removeIf(
					groupBase -> groupBase.getId().equals(groupId));
		} else {
			activeGroup.updateResourceInfo();
		}

		if (activeApp.getActiveGroupMap().isEmpty()) {
			activeAppMap.remove(appId);
			appGroupListMap.remove(appId);
			appBaseList.removeIf(appBase -> appBase.getId().equals(appId));
		}
	}
}
