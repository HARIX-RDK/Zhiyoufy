package com.example.zhiyoufy.server.elk;

import com.example.zhiyoufy.common.elk.ElkData;
import com.example.zhiyoufy.common.elk.ElkRecordBase;
import lombok.Getter;

@Getter
public class ZhiyoufyElkRecord extends ElkRecordBase {
	private ElkData zhiyoufyData;

	@Override
	public void setElkData(ElkData elkData) {
		zhiyoufyData = elkData;
	}
}
