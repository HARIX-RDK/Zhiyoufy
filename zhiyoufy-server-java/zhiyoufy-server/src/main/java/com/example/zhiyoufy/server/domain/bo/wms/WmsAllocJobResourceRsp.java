package com.example.zhiyoufy.server.domain.bo.wms;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsAllocJobResourceRsp {
	Map<String, WmsActiveWorker> jobKeyToWorker;
}
