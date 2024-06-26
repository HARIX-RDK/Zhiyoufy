package com.example.zhiyoufy.common.elk;

public interface IElkSwitchManager {
	boolean isElkSwitchAllOn();
	boolean isElkSwitchOn(String type, String[] tags);
}
