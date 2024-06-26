package com.example.zhiyoufy.server.domain.bo.wms;

import java.util.List;

import com.example.zhiyoufy.server.domain.dto.wms.WmsActiveJobBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsAllocJobResourceReq {
	long appId;
	long groupId;

	List<WmsActiveJobBase> activeJobBaseList;
}
