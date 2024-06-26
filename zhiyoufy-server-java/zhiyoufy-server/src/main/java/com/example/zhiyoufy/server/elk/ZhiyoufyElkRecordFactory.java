package com.example.zhiyoufy.server.elk;

import com.example.zhiyoufy.common.elk.ElkRecordBase;
import com.example.zhiyoufy.common.elk.IElkRecordFactory;

public class ZhiyoufyElkRecordFactory implements IElkRecordFactory {
	@Override
	public ElkRecordBase createElkRecord() {
		return new ZhiyoufyElkRecord();
	}
}
