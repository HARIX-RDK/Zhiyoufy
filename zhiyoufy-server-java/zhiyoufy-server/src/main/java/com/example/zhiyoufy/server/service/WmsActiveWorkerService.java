package com.example.zhiyoufy.server.service;

import java.util.List;

import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerAppBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveWorkerGroupBase;
import com.example.zhiyoufy.server.domain.dto.wms.WmsWorkerRegisterParam;

import org.springframework.messaging.Message;

public interface WmsActiveWorkerService {
	void onWorkerRegister(Message<WmsWorkerRegisterParam> message);
	void disconnectSession(String sessionId);

	List<WmsWorkerAppBase> getAppBaseList();

	List<WmsActiveWorkerGroupBase> getGroupBaseListByAppId(Long appId);

	List<WmsActiveWorkerBase> getWorkerBaseListByGroupId(Long groupId);
}
