package com.example.zhiyoufy.server.manager.activejobrun;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigCollection;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsConfigSingle;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.server.api.ZhiyoufyErrorCode;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveGroup;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceReq;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceRsp;
import com.example.zhiyoufy.server.domain.bo.wms.WmsFreeJobResourceReq;
import com.example.zhiyoufy.server.domain.dto.ems.EmsConfigItemQueryParamForJob;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.domain.event.ActiveWorkerSessionTimeoutEvent;
import com.example.zhiyoufy.server.elasticsearch.JmsJobChildRunResultFullRepository;
import com.example.zhiyoufy.server.elasticsearch.JmsJobRunResultFullRepository;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunActiveWorkerSessionTimeoutEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunJobChildRunResultIndEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStartEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStartJobChildRunRspEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStopEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStopJobChildRunRspEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunUpdatePerfParallelNumEvent;
import com.example.zhiyoufy.server.manager.finishedjobrun.FinishedJobRunResultStore;
import com.example.zhiyoufy.server.mapstruct.JmsJobRunStructMapper;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import com.example.zhiyoufy.server.service.EmsConfigItemService;
import com.example.zhiyoufy.server.service.EmsConfigSingleService;
import com.example.zhiyoufy.server.service.EmsEnvironmentLockService;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
import com.example.zhiyoufy.server.service.PmsProjectService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hubspot.jinjava.Jinjava;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@Getter
@Setter
@Slf4j
public class ActiveJobRunManager implements InitializingBean {
	@Autowired
	ActiveWorkerManager activeWorkerManager;
	@Autowired
	PmsProjectService projectService;
	@Autowired
	PmsJobTemplateService jobTemplateService;
	@Autowired
	WmsWorkerAppService workerAppService;
	@Autowired
	EmsEnvironmentService environmentService;
	@Autowired
	EmsConfigSingleService configSingleService;
	@Autowired
	EmsConfigCollectionService configCollectionService;
	@Autowired
	EmsConfigItemService configItemService;
	@Autowired
	EmsEnvironmentLockService environmentLockService;
	@Autowired
	ExecutorService zhiyoufyExecutorService;
	@Autowired
	JmsJobChildRunResultFullRepository jobChildRunResultFullRepository;
	@Autowired
	JmsJobRunResultFullRepository jobRunResultFullRepository;
	@Autowired
	ScheduledExecutorService zhiyoufyScheduledExecutorService;
	@Autowired
	EventBus eventBus;
	@Autowired
	FinishedJobRunResultStore finishedJobRunResultStore;

	Object retryJobEnd = new Object();
	volatile boolean hasProblemWithDb = false;
	List<JmsActiveJobRun> jobEndRunList = new ArrayList<>();

	Map<String, JmsActiveJobRun> runGuidToRunMap = new ConcurrentHashMap<>();
	List<JmsActiveJobRunBase> runBaseList = new CopyOnWriteArrayList<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		zhiyoufyScheduledExecutorService.scheduleWithFixedDelay(
				this::onTimeoutReleaseExpiredConfigItems,
				10,
				60,
				TimeUnit.SECONDS);

		eventBus.register(this);
	}

	/*
	 * 检查WmsActiveGroup是否在线
	 * 加载jobTemplate，解析config single和config collection列表
	 * 根据输入envId及父子关系加载environment列表
	 * 检查workerGroup空闲资源是否充足
	 * 加载config single并组装
	 * 按照tag配置等分配config items
	 * 组合config，创建childRun
	 */
	public JmsActiveJobRun startJobRun(JmsStartJobRunParam startJobRunParam) {
		String logPrefix = "startJobRun: runGuid " + startJobRunParam.getRunGuid();

		log.info("{} Enter", logPrefix);

		if (hasProblemWithDb) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail("数据库有问题"));
		}

		if(startJobRunParam.getRunNum() < startJobRunParam.getParallelNum()) {
			Asserts.fail(CommonErrorCode.RES_VALIDATE_FAILED.toCustomDetail("参数检验失败"));
		}

		long appId = startJobRunParam.getWorkerAppId();
		long groupId = startJobRunParam.getWorkerGroupId();
		Long jobTemplateId = startJobRunParam.getTemplateId();
		Long environmentId = startJobRunParam.getEnvironmentId();

		WmsActiveGroup activeGroup = activeWorkerManager.getActiveGroup(appId, groupId);

		if (activeGroup == null) {
			Asserts.fail(ZhiyoufyErrorCode.RES_WORKER_GROUP_NOT_ACTIVE);
		}

		WmsWorkerApp workerApp = workerAppService.getWorkerAppById(appId);

		if (workerApp == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("无对应WorkerApp"));
		}

		EmsEnvironment environment = environmentService.getEnvironmentById(environmentId);

		if (environment == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("无对应环境"));
		}

		try {

			Map<String, Object> envWorkerLabels = StrUtils.objectMapper.readValue(environment.getWorkerLabels(),
					new TypeReference<>() {
					});

			Map<String, Object> groupWorkerLabels = StrUtils.objectMapper.readValue(activeGroup.getWorkerGroup().getWorkerLabels(),
					new TypeReference<>() {
					});
			log.info("envWorkerLabels={}", StrUtils.jsonDump(envWorkerLabels));
			log.info("groupWorkerLabels={}", StrUtils.jsonDump(groupWorkerLabels));

			for (String workerLabel: envWorkerLabels.keySet()) {
				if (!groupWorkerLabels.containsKey(workerLabel)) {
					Asserts.fail(ZhiyoufyErrorCode.RES_WORKER_GROUP_ENV_NOT_MATCH);
				}
			}
		}
		catch(Exception e) {
			log.error("{} catch exception :{}", logPrefix, e);
			Asserts.fail(ZhiyoufyErrorCode.RES_WORKER_GROUP_ENV_NOT_MATCH);
		}

		List<EmsEnvironment> environmentList = new ArrayList<>();
		environmentList.add(environment);
		Long parentId = environment.getParentId();

		while (parentId != null && !parentId.equals(0L)) {
			Long finalParentId = parentId;
			Optional<EmsEnvironment> found = environmentList.stream()
					.filter(env -> env.getId().equals(finalParentId)).findAny();

			if (found.isPresent()) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail("父环境依赖存在环路"));
			}

			EmsEnvironment parentEnv = environmentService.getEnvironmentById(parentId);

			if (parentEnv == null) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail("无对应父环境"));
			}

			environmentList.add(parentEnv);

			if (environmentList.size() > 5) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail("父环境依赖长度大于5"));
			}

			parentId = parentEnv.getParentId();
		}

		PmsJobTemplate jobTemplate = jobTemplateService.getJobTemplateById(jobTemplateId);

		if (jobTemplate == null) {
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("无对应JobTemplate"));
		}

		if (CheckUtils.isTrue(jobTemplate.getIsPerf())) {
			if (startJobRunParam.getRunNum() != startJobRunParam.getParallelNum()) {
				Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail(
						"for perf job, runNum should be equal to parallelNum"));
			}
		}

		String configSingles = jobTemplate.getConfigSingles();
		List<String> configSingleNameList;

		if (StringUtils.hasText(configSingles)) {
			configSingleNameList = Arrays.asList(configSingles.split("\\s*,\\s*"));
		} else {
			configSingleNameList = new ArrayList<>();
		}

		String configCollections = jobTemplate.getConfigCollections();
		List<String> configCollectionNameList;

		if (StringUtils.hasText(configCollections)) {
			configCollectionNameList = Arrays.asList(configCollections.split("\\s*,\\s*"));
		} else {
			configCollectionNameList = new ArrayList<>();
		}

		JmsActiveJobRun activeJobRun;

		WmsAllocJobResourceReq allocReq = new WmsAllocJobResourceReq();
		allocReq.setAppId(appId);
		allocReq.setGroupId(groupId);
		List<WmsActiveJobBase> activeJobBaseList = new ArrayList<>();
		allocReq.setActiveJobBaseList(activeJobBaseList);

		for (int i = 0; i < startJobRunParam.getParallelNum(); i++) {
			WmsActiveJobBase activeJobBase = new WmsActiveJobBase();
			activeJobBase.setRunGuid(startJobRunParam.getRunGuid());
			activeJobBase.setIdx(i);

			activeJobBaseList.add(activeJobBase);
		}

		log.info("{} before allocJobResource", logPrefix);

		WmsAllocJobResourceRsp allocRsp = activeWorkerManager.allocJobResource(allocReq);

		if (allocRsp == null) {
			Asserts.fail(ZhiyoufyErrorCode.RES_INSUFFICIENT_WORKER_RESOURCE);
		}

		log.info("{} after allocJobResource", logPrefix);

		boolean reachEnd = false;
		try {
			JmsActiveJobRunBase activeJobRunBase = JmsJobRunStructMapper.INSTANCE
					.startJobRunParamToActiveJobRunBase(startJobRunParam);
			activeJobRunBase.setCreatedOn(new Date());
			if (CheckUtils.isTrue(jobTemplate.getIsPerf())) {
				activeJobRunBase.setPerf(true);
				activeJobRunBase.setDashboardAddr(jobTemplate.getDashboardAddr());
			}
			List<String> workerNames = new ArrayList<>();
			activeJobRunBase.setWorkerNames(workerNames);

			activeJobRun = new JmsActiveJobRun();
			activeJobRun.setActiveJobRunBase(activeJobRunBase);
			activeJobRun.setStartJobRunParam(startJobRunParam);
			activeJobRun.setJobTemplate(jobTemplate);
			activeJobRun.setEnvironment(environment);
			activeJobRun.setConfigCollectionNameList(configCollectionNameList);
			activeJobRun.setNeedConfigBeJson(workerApp.getNeedConfigBeJson());

			// 获取config single value
			StringBuilder configSingleComposite = new StringBuilder();
			StringBuilder dynamicSingleComposite = new StringBuilder();

			for (var configSingleName : configSingleNameList) {
				String configSingleValue = null;
				String dynamicSingleValue = null;

				for (var env : environmentList) {
					EmsConfigSingle configSingle =
							configSingleService.getConfigSingleByEnvIdAndName(
									env.getId(), configSingleName);
					if (configSingle != null) {

						String configValue = configSingle.getConfigValue();
						boolean isTemplate = configValue.contains("#!template");
						boolean renderOnce = configValue.contains("#!renderOnce");

						if (isTemplate && !renderOnce) {
							dynamicSingleValue = "# config dynamic single begin: " + configSingleName + "\n";
							dynamicSingleValue += configValue;
							dynamicSingleValue += "\n# config dynamic single end: " + configSingleName;
							break;
						}
						configSingleValue = "# config single begin: " + configSingleName + "\n";
						if (isTemplate) {
							Jinjava jinjava = new Jinjava();
							String rendered_value = jinjava.render(configValue, null);
							configSingleValue += rendered_value;
						} else {
							configSingleValue += configValue;
						}
						configSingleValue += "\n# config single end: " + configSingleName;
						break;
					}
				}

				if (configSingleValue == null && dynamicSingleValue == null) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail(
							"无法找到Config Single: " + configSingleName));
				}

				if (configSingleValue != null) {
					configSingleComposite.append(configSingleValue).append("\n\n");
				}
				if( dynamicSingleValue != null) {
					dynamicSingleComposite.append(dynamicSingleValue).append("\n\n");
				}
			}

			activeJobRun.setConfigSingleComposite(configSingleComposite.toString());
			activeJobRun.setDynamicSingleComposite(dynamicSingleComposite.toString());

			log.info("{} after compose configSingles", logPrefix);

			allocConfigItems(activeJobRun, jobTemplate, environmentList);

			log.info("{} after allocConfigItems", logPrefix);

			runGuidToRunMap.put(startJobRunParam.getRunGuid(), activeJobRun);
			runBaseList.add(activeJobRunBase);

			activeJobRun.setState(JmsActiveJobRun.State.Running);

			Map<String, List<EmsConfigItem>> configItemListMap = activeJobRun.getConfigItemListMap();
			List<JmsActiveJobChildRun> jobChildRunList;

			for (int i = 0; i < activeJobRun.getParallelNum(); i++) {
				WmsActiveJobBase activeJobBase = activeJobBaseList.get(i);
				WmsActiveWorker activeWorker = allocRsp.getJobKeyToWorker().get(activeJobBase.getJobKey());
				workerNames.add(activeWorker.getActiveWorkerBase().getWorkerName());

				JmsActiveJobChildRun childRun = new JmsActiveJobChildRun(activeJobRun, i);
				childRun.setActiveWorker(activeWorker);

				StringBuilder configItemCompositeBuilder = new StringBuilder();
				for (String configCollectionName : configCollectionNameList) {
					EmsConfigItem configItem = configItemListMap.get(configCollectionName).get(i);
					configItemCompositeBuilder.append("# configCollectionName ")
							.append(configCollectionName).append("\n");
					configItemCompositeBuilder.append("# configItemName ")
							.append(configItem.getName()).append("\n");
					configItemCompositeBuilder.append(configItem.getConfigValue())
							.append("\n\n");
				}
				childRun.setConfigItemComposite(configItemCompositeBuilder.toString());

				activeJobRun.getChildRunList().add(childRun);
			}

			if (activeJobRun.getChildRunList().size() == startJobRunParam.getRunNum()) {
				activeJobRun.setAllChildRunCreated(true);
			} else {
				activeJobRun.setNextChildRunIndex(activeJobRun.getChildRunList().size());
			}

			JobRunStartEvent jobRunStartEvent = new JobRunStartEvent();
			asyncSendEventToActiveJobRun(activeJobRun, jobRunStartEvent);

			log.info("{} after send JobRunStartEvent", logPrefix);

			reachEnd = true;
		} finally {
			if (!reachEnd) {
				WmsFreeJobResourceReq freeReq = new WmsFreeJobResourceReq();
				freeReq.setAppId(appId);
				freeReq.setGroupId(groupId);
				freeReq.setJobKeyToWorker(allocRsp.getJobKeyToWorker());

				activeWorkerManager.freeJobResource(freeReq);
			}
		}

		return activeJobRun;
	}

	@Transactional
	public void allocConfigItems(JmsActiveJobRun activeJobRun, PmsJobTemplate jobTemplate,
			List<EmsEnvironment> environmentList) {
		JmsStartJobRunParam startJobRunParam = activeJobRun.getStartJobRunParam();

		List<Long> envIdList = environmentList.stream().map(EmsEnvironment::getId)
				.collect(Collectors.toList());
		boolean envLocked = false;

		List<String> configCollectionNameList = activeJobRun.getConfigCollectionNameList();
		if (configCollectionNameList.isEmpty()) {
			Map<String, List<EmsConfigItem>> configItemListMap = new HashMap<>();
			List<Long> configItemIdList = new ArrayList<>();
			activeJobRun.setConfigItemListMap(configItemListMap);
			activeJobRun.setConfigItemIdList(configItemIdList);
			return;
		}

		try {
			envLocked = environmentLockService.tryLock(envIdList, 10_000);

			if (!envLocked) {
				Asserts.fail(CommonErrorCode.RES_TIMEOUT.toCustomDetail(
						"超时，无法锁定环境"));
			}

			List<EmsConfigCollection> emsConfigCollectionList =
					configCollectionService.getConfigCollectionList(envIdList, configCollectionNameList);
			Map<String, EmsConfigCollection> envIdCollectionNameToConfigCollection = new HashMap<>();

			for (EmsConfigCollection configCollection : emsConfigCollectionList) {
				String key = configCollection.getName() + "_" + configCollection.getEnvironmentId();
				envIdCollectionNameToConfigCollection.put(key, configCollection);
			}

			Map<String, EmsConfigCollection> collectionNameToCollection = new HashMap<>();

			for (String configCollectionName : configCollectionNameList) {
				EmsConfigCollection configCollection = null;

				for (var env : environmentList) {
					String key = configCollectionName + "_" + env.getId();

					configCollection = envIdCollectionNameToConfigCollection.get(key);

					if (configCollection != null) {
						break;
					}
				}

				if (configCollection == null) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail(
							"无法找到Config Collection: " + configCollectionName));
				}

				collectionNameToCollection.put(configCollectionName, configCollection);
			}

			EmsConfigItemQueryParamForJob queryParam = new EmsConfigItemQueryParamForJob();
			queryParam.setRunNum(startJobRunParam.getRunNum());
			queryParam.setIncludeTags(startJobRunParam.getIncludeTags());
			queryParam.setExcludeTags(startJobRunParam.getExcludeTags());

			Map<String, List<EmsConfigItem>> configItemListMap = new HashMap<>();
			List<Long> configItemIdList = new ArrayList<>();

			for (String configCollectionName : configCollectionNameList) {
				EmsConfigCollection configCollection =
						collectionNameToCollection.get(configCollectionName);

				List<EmsConfigItem> configItemList = configItemService.getConfigItemList(
						configCollection.getId(), queryParam);

				if (configItemList.size() < startJobRunParam.getRunNum()) {
					Asserts.fail(CommonErrorCode.RES_ILLEGAL_STATE.toCustomDetail(
							String.format("可用ConfigItem数量不足，Config Collection: %s, 可用 %d, 需要 %d",
									configCollectionName, configItemList.size(),
									startJobRunParam.getRunNum())));
				}

				configItemListMap.put(configCollectionName, configItemList);

				configItemList.forEach(configItem -> {
					configItemIdList.add(configItem.getId());
				});
			}

			Date usageTimeoutAt = new Date(System.currentTimeMillis() +
					jobTemplate.getTimeoutSeconds() * 1000);
			configItemService.updateConfigItemsForAlloc(configItemIdList,
					startJobRunParam.getRunGuid(), usageTimeoutAt);

			activeJobRun.setConfigItemListMap(configItemListMap);
			activeJobRun.setConfigItemIdList(configItemIdList);
		} finally {
			if (envLocked) {
				environmentLockService.unlock(envIdList);
			}
		}
	}

	public void stopJobRun(String runGuid) {
		String logPrefix = "stopJobRun:";

		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(runGuid);

		if (activeJobRun == null) {
			log.error("{} no activeJobRun found", logPrefix);
			return;
		}

		if (activeJobRun.getState() != JmsActiveJobRun.State.Running) {
			log.error("{} not in Running state", logPrefix);
			return;
		}

		JobRunStopEvent jobRunStopEvent = new JobRunStopEvent();
		sendEventToActiveJobRun(activeJobRun, jobRunStopEvent);
	}

	public void updatePerfParallelNum(JmsJobRunUpdatePerfParallelNumReq updateParallelNumReq) {
		String logPrefix = "updatePerfParallelNum:";

		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(updateParallelNumReq.getRunGuid());

		if (activeJobRun == null) {
			log.error("{} no activeJobRun found", logPrefix);
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("no activeJobRun found"));
		}

		if (activeJobRun.getState() != JmsActiveJobRun.State.Running) {
			log.error("{} not in Running state", logPrefix);
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("not in Running state"));
		}

		if (!activeJobRun.isPerf()) {
			log.error("{} not isPerf", logPrefix);
			Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT.toCustomDetail("not isPerf"));
		}

		JobRunUpdatePerfParallelNumEvent updateParallelNumEvent = new JobRunUpdatePerfParallelNumEvent();
		updateParallelNumEvent.setPerfParallelNum(updateParallelNumReq.getPerfParallelNum());
		sendEventToActiveJobRun(activeJobRun, updateParallelNumEvent);
	}

	public List<JmsActiveJobRunBase> getActiveJobRunBaseList(Boolean allUsers) {
		if (CheckUtils.isTrue(allUsers)) {
			return runBaseList;
		}

		Set<Long> projectIdSet = projectService.getProjectIdSet();

		List<JmsActiveJobRunBase> baseList = new ArrayList<>();

		for (var jobRunBase : runBaseList) {
			if (projectIdSet.contains(jobRunBase.getProjectId())) {
				baseList.add(jobRunBase);
			}
		}

		return baseList;
	}

	public JmsActiveJobRunBase getActiveJobRunBase(String runGuid) {
		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(runGuid);
		if(activeJobRun != null) {
			return activeJobRun.getActiveJobRunBase();
		}
		return null;
	}

	public void asyncSendEventToActiveJobRun(JmsActiveJobRun activeJobRun,
			JobRunEvent jobRunEvent) {
		zhiyoufyExecutorService.submit(() ->
				sendEventToActiveJobRun(activeJobRun, jobRunEvent));
	}

	public void sendEventToActiveJobRun(JmsActiveJobRun activeJobRun, JobRunEvent jobRunEvent) {
		activeJobRun.onEvent(jobRunEvent);
	}

	public void onStartJobChildRunRsp(JmsStartJobChildRunRsp childRunRsp) {
		String logPrefix = "onStartJobChildRunRsp:";

		log.debug("{} Got a message: {}", logPrefix, StrUtils.jsonDump(childRunRsp));

		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(childRunRsp.getRunGuid());

		if (activeJobRun == null) {
			log.error("{} no activeJobRun found", logPrefix);
			return;
		}

		JobRunStartJobChildRunRspEvent event = new JobRunStartJobChildRunRspEvent(childRunRsp);
		sendEventToActiveJobRun(activeJobRun, event);
	}

	public void onStopJobChildRunRsp(JmsStopJobChildRunRsp childRunRsp) {
		String logPrefix = "onStopJobChildRunRsp:";

		log.debug("{} Got a message: {}", logPrefix, StrUtils.jsonDump(childRunRsp));

		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(childRunRsp.getRunGuid());

		if (activeJobRun == null) {
			log.error("{} no activeJobRun found", logPrefix);
			return;
		}

		JobRunStopJobChildRunRspEvent event = new JobRunStopJobChildRunRspEvent(childRunRsp);
		sendEventToActiveJobRun(activeJobRun, event);
	}

	public void onJobChildRunResultInd(JmsJobChildRunResultInd jobChildRunResultInd) {
		String logPrefix = "onJobChildRunResultInd:";

		log.debug("{} Got a message: {}", logPrefix, StrUtils.jsonDump(jobChildRunResultInd));

		JmsActiveJobRun activeJobRun = runGuidToRunMap.get(jobChildRunResultInd.getRunGuid());

		if (activeJobRun == null) {
			log.error("{} no activeJobRun found", logPrefix);
			throw new RuntimeException("no activeJobRun found");
		}

		JobRunJobChildRunResultIndEvent event =
				new JobRunJobChildRunResultIndEvent(jobChildRunResultInd);
		sendEventToActiveJobRun(activeJobRun, event);
	}

	public void onJobRunEnd(JmsActiveJobRun activeJobRun) {
		JmsActiveJobRunBase activeJobRunBase = activeJobRun.getActiveJobRunBase();

		long durationSeconds = (System.currentTimeMillis() -
				activeJobRunBase.getCreatedOn().getTime()) / 1000;

		JmsJobRunResultFull jobRunResultFull = JmsJobRunStructMapper.INSTANCE
				.activeJobRunBaseToJobRunResultFull(activeJobRunBase);
		jobRunResultFull.setDurationSeconds(durationSeconds);
		jobRunResultFull.setTimestamp(new Date());

		if (activeJobRun.getState() == JmsActiveJobRun.State.Stopping) {
			jobRunResultFull.setEndReason("Stopped");
		} else {
			if (activeJobRun.resourceInsufficient) {
				jobRunResultFull.setEndReason("ResourceInsufficient");
			} else {
				jobRunResultFull.setEndReason("Normal");
			}
		}

		jobRunResultFull.setPassed(activeJobRunBase.getPassedNum() == activeJobRunBase
				.getRunNum());

		activeJobRun.setJobRunResultFull(jobRunResultFull);

		try {
			jobRunResultFullRepository.save(jobRunResultFull);
		} catch (Throwable throwable) {
			log.error("elasticsearch save failed", throwable);

			synchronized (retryJobEnd) {
				jobEndRunList.add(activeJobRun);

				if (!hasProblemWithDb) {
					hasProblemWithDb = true;

					zhiyoufyScheduledExecutorService.schedule(
							this::onTimeoutRetryJobEnd,
							10,
							TimeUnit.SECONDS);
				}
			}

			return;
		}

		activeJobRun.setResultSavedToDb(true);

		try {
			configItemService.updateConfigItemsForFreeByUsageId(activeJobRun.getRunGuid());
		} catch (Throwable throwable) {
			log.error("free configItems failed", throwable);

			synchronized (retryJobEnd) {
				jobEndRunList.add(activeJobRun);

				if (!hasProblemWithDb) {
					hasProblemWithDb = true;

					zhiyoufyScheduledExecutorService.schedule(
							this::onTimeoutRetryJobEnd,
							10,
							TimeUnit.SECONDS);
				}
			}

			return;
		}

		runGuidToRunMap.remove(activeJobRun.getRunGuid());
		runBaseList.remove(activeJobRunBase);
		finishedJobRunResultStore.addJobRunResult(activeJobRun.getJobRunResultFull());
	}

	@Subscribe
	public void onActiveWorkerSessionTimeoutEvent(ActiveWorkerSessionTimeoutEvent event) {
		JobRunActiveWorkerSessionTimeoutEvent jobRunEvent =
				new JobRunActiveWorkerSessionTimeoutEvent(event.getActiveWorkerBase());

		for (JmsActiveJobRun activeJobRun : runGuidToRunMap.values()) {
			sendEventToActiveJobRun(activeJobRun, jobRunEvent);
		}
	}

	private void onTimeoutRetryJobEnd() {
		log.info("onTimeoutRetryJobEnd Enter");

		List<JmsActiveJobRun> retryList = new ArrayList<>();
		List<JmsActiveJobRun> failList = new ArrayList<>();

		synchronized (retryJobEnd) {
			retryList.addAll(jobEndRunList);
			jobEndRunList.clear();
		}

		for (JmsActiveJobRun activeJobRun : retryList) {
			if (!activeJobRun.isResultSavedToDb()) {
				try {
					jobRunResultFullRepository.save(activeJobRun.getJobRunResultFull());
				} catch (Throwable throwable) {
					log.error("elasticsearch save failed", throwable);

					failList.add(activeJobRun);

					continue;
				}

				activeJobRun.setResultSavedToDb(true);
			}

			try {
				configItemService.updateConfigItemsForFreeByUsageId(activeJobRun.getRunGuid());
			} catch (Throwable throwable) {
				log.error("free configItems failed", throwable);

				failList.add(activeJobRun);

				continue;
			}

			runGuidToRunMap.remove(activeJobRun);
			runBaseList.remove(activeJobRun.getActiveJobRunBase());
		}

		synchronized (retryJobEnd) {
			jobEndRunList.addAll(failList);

			if (jobEndRunList.size() > 0) {
				zhiyoufyScheduledExecutorService.schedule(
						this::onTimeoutRetryJobEnd,
						10,
						TimeUnit.SECONDS);
			} else {
				hasProblemWithDb = false;
			}
		}
	}

	private void onTimeoutReleaseExpiredConfigItems() {
		log.info("onTimeoutReleaseExpiredConfigItems Enter");

		try {
			configItemService.updateConfigItemsForFreeByUsageTimeoutAt(new Date());
		} catch (Throwable throwable) {
			log.error("free configItems failed", throwable);
		}
	}
}
