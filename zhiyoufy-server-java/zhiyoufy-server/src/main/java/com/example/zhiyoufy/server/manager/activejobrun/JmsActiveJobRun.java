package com.example.zhiyoufy.server.manager.activejobrun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.EmsConfigItem;
import com.example.zhiyoufy.mbg.model.EmsEnvironment;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.config.SpringContext;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceReq;
import com.example.zhiyoufy.server.domain.bo.wms.WmsAllocJobResourceRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunActiveWorkerSessionTimeoutEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunJobChildRunErrorEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunJobChildRunResultIndEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStartEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStartJobChildRunRspEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStopEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunStopJobChildRunRspEvent;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunUpdatePerfParallelNumEvent;
import com.example.zhiyoufy.server.mapstruct.JmsJobRunStructMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class JmsActiveJobRun {
	JmsActiveJobRunBase activeJobRunBase;

	JmsStartJobRunParam startJobRunParam;
	PmsJobTemplate jobTemplate;
	EmsEnvironment environment;

	State state = State.Idle;
	String message = "";
	String logPrefix = null;

	ReentrantLock lock = new ReentrantLock();

	String configSingleComposite;
	String dynamicSingleComposite;
	List<String> configCollectionNameList;
	Map<String, List<EmsConfigItem>> configItemListMap;
	List<Long> configItemIdList;
	Boolean needConfigBeJson;

	boolean allChildRunCreated;
	int nextChildRunIndex;
	boolean resourceInsufficient;
	List<JmsActiveJobChildRun> childRunList = new ArrayList<>();

	JmsJobRunResultFull jobRunResultFull;
	boolean resultSavedToDb;

	public String getRunGuid() {
		return startJobRunParam.getRunGuid();
	}

	public int getRunNum() {
		return startJobRunParam.getRunNum();
	}

	public int getParallelNum() {
		return startJobRunParam.getParallelNum();
	}

	public boolean isPerf() {
		return activeJobRunBase.isPerf();
	}

	public String getLogPrefix() {
		if (logPrefix == null) {
			logPrefix = "job-run-" + getRunGuid();
		}
		return logPrefix;
	}

	public void setState(State state) {
		if (this.state != state) {
			log.debug("{} state change from {} to {}", getLogPrefix(), this.state, state);

			activeJobRunBase.setState(state.name());

			this.state = state;
		}
	}

	public void onEvent(JobRunEvent event) {
		lock.lock();
		try {
			log.info("{} state {}, receive event {}", getLogPrefix(), state, StrUtils.jsonDump(event));

			if (event.getEventType().equals(JobRunStartJobChildRunRspEvent.EVENT_TYPE)) {
				onEventStateAnyJobRunStartJobChildRunRsp((JobRunStartJobChildRunRspEvent)event);
			} else if (event.getEventType().equals(JobRunStopJobChildRunRspEvent.EVENT_TYPE)) {
				onEventStateAnyJobRunStopJobChildRunRsp((JobRunStopJobChildRunRspEvent)event);
			} else if (event.getEventType().equals(JobRunJobChildRunResultIndEvent.EVENT_TYPE)) {
				onEventStateAnyJobChildRunResultInd((JobRunJobChildRunResultIndEvent)event);
			} else if (event.getEventType().equals(JobRunJobChildRunErrorEvent.EVENT_TYPE)) {
				onEventStateAnyJobChildRunError((JobRunJobChildRunErrorEvent)event);
			} else {
				if (state == State.Running) {
					onEventStateRunning(event);
				} else if (state == State.Stopping) {
					onEventStateStopping(event);
				} else {
					log.warn("{} unexpected state {}, ignore event", getLogPrefix(), state);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private void onEventStateRunning(JobRunEvent event) {
		if (event.getEventType().equals(JobRunStartEvent.EVENT_TYPE)) {
			onEventStateRunningJobRunStart((JobRunStartEvent) event);
		} else if (event.getEventType().equals(JobRunStopEvent.EVENT_TYPE)) {
			onEventStateRunningJobRunStop((JobRunStopEvent)event);
		} else if (event.getEventType().equals(JobRunUpdatePerfParallelNumEvent.EVENT_TYPE)) {
			onEventStateRunningUpdatePerfParallelNum((JobRunUpdatePerfParallelNumEvent)event);
		} else if (event.getEventType().equals(JobRunActiveWorkerSessionTimeoutEvent.EVENT_TYPE)) {
			onEventStateRunningActiveWorkerSessionTimeout((JobRunActiveWorkerSessionTimeoutEvent)event);
		} else {
			log.warn("{} unexpected event type {}, ignore event",
					getLogPrefix(), event.getEventType());
		}
	}

	private void onEventStateStopping(JobRunEvent event) {
		log.warn("{} unexpected event type {}, ignore event",
				getLogPrefix(), event.getEventType());
	}

	private void onEventStateRunningJobRunStart(JobRunStartEvent event) {
		for (var childRun : childRunList) {
			childRun.sendStartJobReq();
		}
		activeJobRunBase.setStartedNum(activeJobRunBase.getStartedNum() + childRunList.size());
		activeJobRunBase.setActiveNum(activeJobRunBase.getActiveNum() + childRunList.size());
	}

	private void onEventStateAnyJobRunStartJobChildRunRsp(JobRunStartJobChildRunRspEvent event) {
		JmsStartJobChildRunRsp childRunRsp = event.getStartJobChildRunRsp();

		JmsActiveJobChildRun childRun = getChildRunByIndex(childRunRsp.getIndex());

		if (childRun == null) {
			log.error("{} no childRun found", getLogPrefix());
			return;
		}

		childRun.onStartJobRsp(event.getStartJobChildRunRsp());
	}

	private void onEventStateAnyJobRunStopJobChildRunRsp(JobRunStopJobChildRunRspEvent event) {
		JmsStopJobChildRunRsp childRunRsp = event.getStopJobChildRunRsp();

		JmsActiveJobChildRun childRun = getChildRunByIndex(childRunRsp.getIndex());

		if (childRun == null) {
			log.error("{} no childRun found", getLogPrefix());
			return;
		}

		childRun.onStopJobRsp();
	}

	private void onEventStateAnyJobChildRunResultInd(JobRunJobChildRunResultIndEvent event) {
		JmsJobChildRunResultInd childRunResultInd = event.getJobChildRunResultInd();

		JmsActiveJobChildRun childRun = getChildRunByIndex(childRunResultInd.getIndex());

		if (childRun == null) {
			log.error("{} no childRun found", getLogPrefix());
			throw new RuntimeException("no childRun found");
		}

		childRun.onJobChildRunResultInd(childRunResultInd);

		JmsJobChildRunResultFull jobChildRunResultFull = JmsJobRunStructMapper.INSTANCE
				.activeJobRunBaseToJobChildRunResultFull(activeJobRunBase);
		JmsJobRunStructMapper.INSTANCE.updateJobChildRunResultFullFromJobChildRunResultInd(
				childRunResultInd, jobChildRunResultFull);
		jobChildRunResultFull.setId(jobChildRunResultFull.getRunGuid() +
				"-index-" + jobChildRunResultFull.getIndex());
		jobChildRunResultFull.setTimestamp(new Date());

		jobChildRunResultFull.setEndReason("Normal");

		saveResultToElasticsearch(jobChildRunResultFull);

		childRunList.remove(childRun);

		activeJobRunBase.setActiveNum(activeJobRunBase.getActiveNum() - 1);
		activeJobRunBase.setFinishedNum(activeJobRunBase.getFinishedNum() + 1);

		if (childRunResultInd.getPassed()) {
			activeJobRunBase.setPassedNum(activeJobRunBase.getPassedNum() + 1);
		}

		onChildJobRunEnd();
	}

	private void onEventStateAnyJobChildRunError(JobRunJobChildRunErrorEvent event) {
		JmsActiveJobChildRun childRun = getChildRunByIndex(event.getIndex());

		if (childRun == null) {
			log.error("{} no childRun found", getLogPrefix());
			return;
		}

		onChildJobRunEndWithError(childRun, event.getReason().name());
	}

	private void onEventStateRunningJobRunStop(JobRunStopEvent event) {
		setState(State.Stopping);

		for (var childRun : childRunList) {
			childRun.sendStopJobReq();
		}
	}

	private void onEventStateRunningUpdatePerfParallelNum(JobRunUpdatePerfParallelNumEvent event) {
		activeJobRunBase.setPerfParallelNum(event.getPerfParallelNum());

		JmsJobRunUpdatePerfParallelNumReq req = new JmsJobRunUpdatePerfParallelNumReq();
		req.setRunGuid(getRunGuid());
		req.setPerfParallelNum(activeJobRunBase.getPerfParallelNum());
		for (var childRun : childRunList) {
			childRun.sendUpdatePerfParallelNumReq(req);
		}
	}

	private void onEventStateRunningActiveWorkerSessionTimeout(
			JobRunActiveWorkerSessionTimeoutEvent event) {
		List<JmsActiveJobChildRun> childRunListCopy = new ArrayList<>(childRunList);

		for (var childRun : childRunListCopy) {
			boolean childEnd = childRun.onActiveWorkerSessionTimeout(event.getActiveWorkerBase());

			if (childEnd) {
				onChildJobRunEndWithError(childRun, "ActiveWorkerSessionTimeout");
			}
		}
	}

	private void startMoreIfNeeded() {
		String localLogPrefix = getLogPrefix() + " startMoreIfNeeded:";

		if (allChildRunCreated || resourceInsufficient) {
			log.error("{} no need to start more, allChildRunCreated: {}, resourceInsufficient: {}",
					localLogPrefix, allChildRunCreated, resourceInsufficient);
			return;
		}

		List<WmsActiveJobBase> activeJobBaseList = new ArrayList<>();
		WmsActiveJobBase activeJobBase = new WmsActiveJobBase();
		activeJobBase.setRunGuid(startJobRunParam.getRunGuid());
		activeJobBase.setIdx(nextChildRunIndex);
		activeJobBaseList.add(activeJobBase);

		JmsActiveJobRunBase activeJobRunBase = getActiveJobRunBase();
		WmsAllocJobResourceReq allocReq = new WmsAllocJobResourceReq();
		allocReq.setAppId(activeJobRunBase.getWorkerAppId());
		allocReq.setGroupId(activeJobRunBase.getWorkerGroupId());
		allocReq.setActiveJobBaseList(activeJobBaseList);

		WmsAllocJobResourceRsp allocRsp = SpringContext.getInstance().getActiveWorkerManager().allocJobResource(allocReq);
		if (allocRsp == null) {
			log.error("{} no active worker available", localLogPrefix);
			resourceInsufficient = true;
			return;
		}

		WmsActiveWorker activeWorker = allocRsp.getJobKeyToWorker().get(activeJobBase.getJobKey());

		JmsActiveJobChildRun childRun = new JmsActiveJobChildRun(this, nextChildRunIndex);
		childRun.setActiveWorker(activeWorker);

		StringBuilder configItemCompositeBuilder = new StringBuilder();
		for (String configCollectionName : configCollectionNameList) {
			EmsConfigItem configItem = configItemListMap.get(configCollectionName).get(nextChildRunIndex);
			configItemCompositeBuilder.append("# configCollectionName ")
					.append(configCollectionName).append("\n");
			configItemCompositeBuilder.append("# configItemName ")
					.append(configItem.getName()).append("\n");
			configItemCompositeBuilder.append(configItem.getConfigValue())
					.append("\n\n");
		}
		childRun.setConfigItemComposite(configItemCompositeBuilder.toString());

		childRunList.add(childRun);

		if ((nextChildRunIndex + 1) == startJobRunParam.getRunNum()) {
			allChildRunCreated = true;
		} else {
			nextChildRunIndex ++;
		}

		childRun.sendStartJobReq();

		activeJobRunBase.setStartedNum(activeJobRunBase.getStartedNum() + 1);
		activeJobRunBase.setActiveNum( childRunList.size());

	}

	private void onChildJobRunEndWithError(JmsActiveJobChildRun jobChildRun, String reason) {
		String localLogPrefix = getLogPrefix() + " onChildJobRunEndWithError:";

		if (!childRunList.contains(jobChildRun)) {
			log.info("{} no childRun found", localLogPrefix);
			return;
		}

		jobChildRun.onErrorEnd();

		JmsJobChildRunResultFull jobChildRunResultFull = JmsJobRunStructMapper.INSTANCE
				.activeJobRunBaseToJobChildRunResultFull(activeJobRunBase);
		jobChildRunResultFull.setIndex(jobChildRun.getIndex());
		jobChildRunResultFull.setId(jobChildRunResultFull.getRunGuid() +
				"-index-" + jobChildRunResultFull.getIndex());
		jobChildRunResultFull.setTimestamp(new Date());

		jobChildRunResultFull.setEndReason(reason);
		jobChildRunResultFull.setEndDetail(jobChildRun.getEndDetail());
		jobChildRunResultFull.setPassed(false);

		saveResultToElasticsearch(jobChildRunResultFull);

		childRunList.remove(jobChildRun);

		activeJobRunBase.setActiveNum(activeJobRunBase.getActiveNum() - 1);
		activeJobRunBase.setFinishedNum(activeJobRunBase.getFinishedNum() + 1);

		onChildJobRunEnd();
	}

	private void onChildJobRunEnd() {
		if (state == State.Running) {
			startMoreIfNeeded();

			if ((allChildRunCreated || resourceInsufficient) && childRunList.size() == 0) {
				onJobRunEnd();
			}
		} else {
			if (childRunList.size() == 0) {
				onJobRunEnd();
			}
		}
	}

	private void onJobRunEnd() {
		ActiveJobRunManager activeJobRunManager = SpringContext.getInstance().getActiveJobRunManager();
		activeJobRunManager.onJobRunEnd(this);

		log.info("{} onJobRunEnd Leave {}", getLogPrefix(), StrUtils.jsonDump(activeJobRunBase));
	}

	private JmsActiveJobChildRun getChildRunByIndex(int index) {
		JmsActiveJobChildRun found = null;

		for (var childRun : childRunList) {
			if (childRun.getIndex() == index) {
				found = childRun;
				break;
			}
		}

		return found;
	}

	private void saveResultToElasticsearch(JmsJobChildRunResultFull jobChildRunResultFull) {
		int maxTrialNum = 3;

		for (int trial=0; trial < maxTrialNum; trial++) {
			try {
				SpringContext.getInstance().getJobChildRunResultFullRepository()
						.save(jobChildRunResultFull);
				break;
			} catch (Throwable throwable) {
				log.error("trial {}, elasticsearch save failed", trial, throwable);
			}
		}
	}

	public enum State {
		Idle,
		Running,
		Stopping,
		Finished;
	}
}
