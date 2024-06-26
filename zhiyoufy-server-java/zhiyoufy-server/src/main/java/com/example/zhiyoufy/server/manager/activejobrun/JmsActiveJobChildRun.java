package com.example.zhiyoufy.server.manager.activejobrun;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.mbg.model.PmsJobTemplate;
import com.example.zhiyoufy.server.config.SpringContext;
import com.example.zhiyoufy.server.config.ZhiyoufyServerProperties;
import com.example.zhiyoufy.server.domain.bo.wms.WmsActiveWorker;
import com.example.zhiyoufy.server.domain.bo.wms.WmsFreeJobResourceReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStopJobChildRunReq;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.manager.activejobrun.event.JobRunJobChildRunErrorEvent;
import com.hubspot.jinjava.Jinjava;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Slf4j
public class JmsActiveJobChildRun {
	final JmsActiveJobRun activeJobRun;
	private final int index;
	final WmsActiveJobBase activeJobBase;

	WmsActiveWorker activeWorker;
	String configItemComposite;
	String endDetail;

	boolean startJobReqSent;
	ScheduledFuture<?> waitStartJobRspFuture;
	boolean startJobRspReceived;

	ScheduledFuture<?> waitResultIndFuture;

	boolean stopJobReqSent;
	ScheduledFuture<?> waitStopJobRspFuture;
	boolean stopJobRspReceived;

	final Object futureLock = new Object();

	String logPrefix = null;

	public JmsActiveJobChildRun(JmsActiveJobRun activeJobRun, int index) {
		this.activeJobRun = activeJobRun;
		this.index = index;

		activeJobBase = new WmsActiveJobBase();
		activeJobBase.setRunGuid(activeJobRun.getRunGuid());
		activeJobBase.setIdx(index);
	}

	public String getLogPrefix() {
		if (logPrefix == null) {
			logPrefix = "job-child-run-" + activeJobRun.getRunGuid() + "-index-" + index;
		}
		return logPrefix;
	}

	public void sendStartJobReq() {
		doSendStartJobReq();

		if (!startJobReqSent) {
			return;
		}

		waitStartJobRspFuture = getScheduledExecutorService().schedule(
				this::onTimeoutWaitStartJobRsp,
				getZhiyoufyProperties().getDefaultReplyTimeout(),
				TimeUnit.SECONDS);
	}

	public void onConvertHoconToJsonFailed() {
		JobRunJobChildRunErrorEvent event = new JobRunJobChildRunErrorEvent(
				JobRunJobChildRunErrorEvent.Reason.ConvertHoconToJsonFailed, index);
		activeJobRun.onEvent(event);
	}

	public void onTimeoutWaitStartJobRsp() {
		synchronized (futureLock) {
			waitStartJobRspFuture = null;
		}

		log.error("{} onTimeoutWaitStartJobRsp", getLogPrefix());

		JobRunJobChildRunErrorEvent event = new JobRunJobChildRunErrorEvent(
				JobRunJobChildRunErrorEvent.Reason.WaitStartJobRspTimeout, index);
		activeJobRun.onEvent(event);
	}

	public boolean onActiveWorkerSessionTimeout(WmsActiveWorkerBase activeWorkerBase) {
		if (activeWorker.getActiveWorkerBase().getSessionId().equals(activeWorkerBase.getSessionId())) {
			return true;
		}

		return false;
	}

	public void onErrorEnd() {
		log.info("{} onErrorEnd", getLogPrefix());

		synchronized (futureLock) {
			if (waitStartJobRspFuture != null) {
				waitStartJobRspFuture.cancel(false);
				waitStartJobRspFuture = null;
			}

			if (waitResultIndFuture != null) {
				waitResultIndFuture.cancel(false);
				waitResultIndFuture = null;
			}

			if (waitStopJobRspFuture != null) {
				waitStopJobRspFuture.cancel(false);
				waitStopJobRspFuture = null;
			}
		}

		doReleaseResource();
	}

	public void onStartJobRsp(JmsStartJobChildRunRsp startJobChildRunRsp) {
		synchronized (futureLock) {
			if (waitStartJobRspFuture != null) {
				waitStartJobRspFuture.cancel(false);
				waitStartJobRspFuture = null;
			}
		}

		startJobRspReceived = true;

		if (startJobChildRunRsp.getError() != null) {
			log.error("{} onStartJobRsp rsp error {}", getLogPrefix(),
					StrUtils.jsonDump(startJobChildRunRsp.getError()));

			JobRunJobChildRunErrorEvent event = new JobRunJobChildRunErrorEvent(
					JobRunJobChildRunErrorEvent.Reason.StartJobChildRunFailed, index);
			activeJobRun.onEvent(event);
		} else {
			waitResultIndFuture = getScheduledExecutorService().schedule(
					this::onTimeoutWaitResultInd,
					activeJobRun.getJobTemplate().getTimeoutSeconds(),
					TimeUnit.SECONDS);
		}
	}

	public void onTimeoutWaitResultInd() {
		synchronized (futureLock) {
			waitResultIndFuture = null;
		}

		log.error("{} onTimeoutWaitResultInd", getLogPrefix());

		JobRunJobChildRunErrorEvent event = new JobRunJobChildRunErrorEvent(
				JobRunJobChildRunErrorEvent.Reason.WaitResultIndTimeout, index);
		activeJobRun.onEvent(event);
	}

	public void onJobChildRunResultInd(JmsJobChildRunResultInd childRunResultInd) {
		synchronized (futureLock) {
			if (waitResultIndFuture != null) {
				waitResultIndFuture.cancel(false);
				waitResultIndFuture = null;
			}
		}

		doSendJobChildRunResultRsp(childRunResultInd);

		doReleaseResource();
	}

	public void sendStopJobReq() {
		doSendStopJobReq();

		waitStopJobRspFuture = getScheduledExecutorService().schedule(
				this::onTimeoutWaitStopJobRsp,
				getZhiyoufyProperties().getActiveWorkerSessionTimeout(),
				TimeUnit.SECONDS);
	}

	public void sendUpdatePerfParallelNumReq(JmsJobRunUpdatePerfParallelNumReq req) {
		log.info("{} to send update-perf-parallel-num-req", getLogPrefix());

		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setSessionId(activeWorker.getActiveWorkerBase().getSessionId());
		accessor.setSubscriptionId("0");

		MessageHeaders headersToSend = accessor.getMessageHeaders();

		String destination = "/app/update-perf-parallel-num-req";

		SimpMessagingTemplate clientMessagingTemplate =
				SpringContext.getInstance().getClientMessagingTemplate();
		clientMessagingTemplate.convertAndSend(destination, req, headersToSend);
	}

	public void onTimeoutWaitStopJobRsp() {
		synchronized (futureLock) {
			waitStopJobRspFuture = null;
		}

		log.error("{} onTimeoutWaitStopJobRsp", getLogPrefix());

		JobRunJobChildRunErrorEvent event = new JobRunJobChildRunErrorEvent(
				JobRunJobChildRunErrorEvent.Reason.WaitStopJobRspTimeout, index);
		activeJobRun.onEvent(event);
	}

	public void onStopJobRsp() {
		synchronized (futureLock) {
			if (waitStopJobRspFuture != null) {
				waitStopJobRspFuture.cancel(false);
				waitStopJobRspFuture = null;
			}
		}

		stopJobRspReceived = true;
	}

	private ZhiyoufyServerProperties getZhiyoufyProperties() {
		return SpringContext.getInstance().getZhiyoufyServerProperties();
	}

	private ScheduledExecutorService getScheduledExecutorService() {
		return SpringContext.getInstance().getZhiyoufyScheduledExecutorService();
	}

	private void doSendStartJobReq() {
		log.info("{} to send start-job-child-run-req", getLogPrefix());

		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setSessionId(activeWorker.getActiveWorkerBase().getSessionId());
		accessor.setSubscriptionId("0");

		MessageHeaders headersToSend = accessor.getMessageHeaders();

		String destination = "/app/start-job-child-run-req";

		JmsStartJobChildRunReq childRunReq = new JmsStartJobChildRunReq();

		JmsStartJobRunParam startJobRunParam = activeJobRun.getStartJobRunParam();
		childRunReq.setRunGuid(startJobRunParam.getRunGuid());
		childRunReq.setEnvironmentId(startJobRunParam.getEnvironmentId());
		childRunReq.setEnvironmentName(startJobRunParam.getEnvironmentName());
		childRunReq.setTemplateId(startJobRunParam.getTemplateId());
		childRunReq.setTemplateName(startJobRunParam.getTemplateName());
		childRunReq.setRunNum(startJobRunParam.getRunNum());
		childRunReq.setParallelNum(startJobRunParam.getParallelNum());

		childRunReq.setIndex(index);

		PmsJobTemplate jobTemplate = activeJobRun.getJobTemplate();
		childRunReq.setJobPath(jobTemplate.getJobPath());
		childRunReq.setTimeoutSeconds(jobTemplate.getTimeoutSeconds());
		childRunReq.setBaseConfPath(jobTemplate.getBaseConfPath());
		childRunReq.setPrivateConfPath(jobTemplate.getPrivateConfPath());

		String configComposite = activeJobRun.getConfigSingleComposite() + configItemComposite;
		if (StringUtils.hasText(activeJobRun.getDynamicSingleComposite())) {
			Jinjava jinjava = new Jinjava();
			String rendered_value = jinjava.render(activeJobRun.getDynamicSingleComposite(), null);
			configComposite += rendered_value;
		}
		if (activeJobRun.getNeedConfigBeJson() || activeJobRun.isPerf()) {
			try {
				Config config = ConfigFactory.parseString(configComposite);
				config = config.resolve();
				ConfigRenderOptions options =
						ConfigRenderOptions.concise().setFormatted(true);
				configComposite = config.root().render(options);

				if (activeJobRun.isPerf()) {
					// ConfigException.Missing
					// ConfigException.WrongType
					int perfParallelNum = config.getInt("jobRunner.parallelNum");

					if (perfParallelNum < 1) {
						throw new RuntimeException(String.format(
								"jobRunner.parallelNum %d is less than 1", perfParallelNum));
					}

					if (activeJobRun.getActiveJobRunBase().getPerfParallelNum() < 1) {
						activeJobRun.getActiveJobRunBase().setPerfParallelNum(perfParallelNum);
					}
				}
			} catch (Exception ex) {
				log.info("{} in configComposite {}", getLogPrefix(), configComposite);
				log.error("{} convertHoconToJsonFailed: {}", getLogPrefix(), ex);

				endDetail = String.format("exception %s\n\nconfigComposite\n%s", ex, configComposite);

				getScheduledExecutorService().schedule(
						this::onConvertHoconToJsonFailed,
						0,
						TimeUnit.SECONDS);
				return;
			}
		}
		childRunReq.setConfigComposite(configComposite);

		String extraArgs = "";

		if (StringUtils.hasText(jobTemplate.getExtraArgs())) {
			extraArgs += jobTemplate.getExtraArgs();
		}

		if (StringUtils.hasText(activeJobRun.getEnvironment().getExtraArgs())) {
			extraArgs += " " + activeJobRun.getEnvironment().getExtraArgs();
		}

		childRunReq.setExtraArgs(extraArgs);

		SimpMessagingTemplate clientMessagingTemplate =
				SpringContext.getInstance().getClientMessagingTemplate();
		clientMessagingTemplate.convertAndSend(destination, childRunReq, headersToSend);

		startJobReqSent = true;
	}

	private void doSendStopJobReq() {
		log.info("{} to send stop-job-child-run-req", getLogPrefix());

		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setSessionId(activeWorker.getActiveWorkerBase().getSessionId());
		accessor.setSubscriptionId("0");

		MessageHeaders headersToSend = accessor.getMessageHeaders();

		String destination = "/app/stop-job-child-run-req";

		JmsStopJobChildRunReq childRunReq = new JmsStopJobChildRunReq();

		JmsStartJobRunParam startJobRunParam = activeJobRun.getStartJobRunParam();
		childRunReq.setRunGuid(startJobRunParam.getRunGuid());
		childRunReq.setIndex(index);

		SimpMessagingTemplate clientMessagingTemplate =
				SpringContext.getInstance().getClientMessagingTemplate();
		clientMessagingTemplate.convertAndSend(destination, childRunReq, headersToSend);

		stopJobReqSent = true;
	}

	private void doSendJobChildRunResultRsp(JmsJobChildRunResultInd childRunResultInd) {
		log.info("{} to send job-child-run-result-rsp", getLogPrefix());

		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setSessionId(activeWorker.getActiveWorkerBase().getSessionId());
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

		startJobReqSent = true;
	}

	private void doReleaseResource() {
		ActiveWorkerManager activeWorkerManager = SpringContext.getInstance()
				.getActiveWorkerManager();

		WmsFreeJobResourceReq freeJobResourceReq = new WmsFreeJobResourceReq();
		freeJobResourceReq.setAppId(activeWorker.getWorkerApp().getId());
		freeJobResourceReq.setGroupId(activeWorker.getWorkerGroup().getId());

		Map<String, WmsActiveWorker> jobKeyToWorker = new HashMap<>();
		jobKeyToWorker.put(activeJobBase.getJobKey(), activeWorker);

		freeJobResourceReq.setJobKeyToWorker(jobKeyToWorker);

		activeWorkerManager.freeJobResource(freeJobResourceReq);
	}
}
