package com.example.zhiyoufy.server.domain.bo.wms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.example.zhiyoufy.mbg.model.WmsWorkerApp;
import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;
import lombok.Getter;
import lombok.Setter;

import org.springframework.util.Assert;

@Getter
@Setter
public class WmsActiveWorker {
	private WmsActiveWorkerBase activeWorkerBase;

	private WmsActiveGroup activeGroup;

	private WmsWorkerApp workerApp;
	private WmsWorkerGroup workerGroup;

	private WmsWorkerRegisterParam registerParam;

	private ScheduledFuture<?> sessionTimeoutFuture;
	private Map<String, WmsActiveJobBase> jobKeyToJobBase = new HashMap<>();

	public void addActiveJobBase(WmsActiveJobBase activeJobBase) {
		Assert.state(!jobKeyToJobBase.containsKey(activeJobBase.getJobKey()),
				"JobKey " + activeJobBase.getJobKey() + " already exist");

		jobKeyToJobBase.put(activeJobBase.getJobKey(), activeJobBase);
		activeWorkerBase.setFreeActiveJobNum(activeWorkerBase.getFreeActiveJobNum() - 1);
	}

	public void removeActiveJobBase(String jobKey) {
		Assert.state(jobKeyToJobBase.containsKey(jobKey),
				"JobKey " + jobKey + " not exist");

		jobKeyToJobBase.remove(jobKey);
		activeWorkerBase.setFreeActiveJobNum(activeWorkerBase.getFreeActiveJobNum() + 1);
	}

	public String getWorkerKey() {
		return String.format("app-%d-group-%d-worker-%s",
				workerApp.getId(), workerGroup.getId(), registerParam.getWorkerName());
	}
}
