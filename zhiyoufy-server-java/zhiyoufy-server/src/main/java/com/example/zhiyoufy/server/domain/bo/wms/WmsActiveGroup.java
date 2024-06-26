package com.example.zhiyoufy.server.domain.bo.wms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.example.zhiyoufy.mbg.model.WmsWorkerGroup;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import lombok.Getter;
import lombok.Setter;

import org.springframework.util.Assert;

@Getter
@Setter
public class WmsActiveGroup {
	private WmsActiveWorkerGroupBase workerGroupBase;

	private WmsWorkerGroup workerGroup;

	private ReentrantLock lock = new ReentrantLock();
	private Map<String, WmsActiveWorker> activeWorkerMap = new ConcurrentHashMap<>();

	public WmsActiveGroup(WmsWorkerGroup workerGroup) {
		this.workerGroup = workerGroup;
	}

	public void updateResourceInfo() {
		int maxActiveJobNum = 0;
		int freeActiveJobNum = 0;

		for (WmsActiveWorker activeWorker : activeWorkerMap.values()) {
			WmsActiveWorkerBase activeWorkerBase = activeWorker.getActiveWorkerBase();

			if (activeWorkerBase.isDisconnected()) {
				continue;
			}

			maxActiveJobNum += activeWorkerBase.getMaxActiveJobNum();
			freeActiveJobNum += activeWorkerBase.getFreeActiveJobNum();
		}

		workerGroupBase.setMaxActiveJobNum(maxActiveJobNum);
		workerGroupBase.setFreeActiveJobNum(freeActiveJobNum);
	}

	public WmsAllocJobResourceRsp allocJobResource(WmsAllocJobResourceReq req) {
		List<WmsActiveJobBase> activeJobBaseList = req.activeJobBaseList;

		if (workerGroupBase.getFreeActiveJobNum() < activeJobBaseList.size()) {
			return null;
		}

		WmsAllocJobResourceRsp rsp = new WmsAllocJobResourceRsp();
		Map<String, WmsActiveWorker> jobKeyToWorker = new HashMap<>();
		rsp.setJobKeyToWorker(jobKeyToWorker);

		int nextIdx = 0;
		int leftNeededNum = activeJobBaseList.size();

		while (leftNeededNum > 0) {
			WmsActiveWorker mostFreeWorker = getMostFreeWorker();

			Assert.state(mostFreeWorker.getActiveWorkerBase().getFreeActiveJobNum() > 0,
					"MostFreeWorker is unexpected full");

			int turnNum = 5;

			if (leftNeededNum < turnNum) {
				turnNum = leftNeededNum;
			}

			if (mostFreeWorker.getActiveWorkerBase().getFreeActiveJobNum() < turnNum) {
				turnNum = mostFreeWorker.getActiveWorkerBase().getFreeActiveJobNum();
			}

			for (int i = 0; i < turnNum; i++) {
				WmsActiveJobBase activeJobBase = activeJobBaseList.get(nextIdx + i);

				mostFreeWorker.addActiveJobBase(activeJobBase);
				jobKeyToWorker.put(activeJobBase.getJobKey(), mostFreeWorker);
			}

			nextIdx += turnNum;
			leftNeededNum -= turnNum;
		}

		updateResourceInfo();

		return rsp;
	}

	public void freeJobResource(WmsFreeJobResourceReq req) {
		Map<String, WmsActiveWorker> jobKeyToWorker = req.getJobKeyToWorker();

		for (var item : jobKeyToWorker.entrySet()) {
			String jobKey = item.getKey();
			WmsActiveWorker activeWorker = item.getValue();

			activeWorker.removeActiveJobBase(jobKey);
		}

		updateResourceInfo();
	}

	private WmsActiveWorker getMostFreeWorker() {
		WmsActiveWorker mostFreeWorker = null;

		for (WmsActiveWorker activeWorker : activeWorkerMap.values()) {
			WmsActiveWorkerBase activeWorkerBase = activeWorker.getActiveWorkerBase();

			if (activeWorkerBase.isDisconnected()) {
				continue;
			}

			if (mostFreeWorker == null) {
				mostFreeWorker = activeWorker;
			} else if (activeWorkerBase.getFreeActiveJobNum() >
					mostFreeWorker.getActiveWorkerBase().getFreeActiveJobNum()) {
				mostFreeWorker = activeWorker;
			}
		}

		return mostFreeWorker;
	}
}
