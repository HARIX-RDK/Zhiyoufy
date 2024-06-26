package com.example.zhiyoufy.common.elk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.zhiyoufy.common.config.ZhiyoufyCommonProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * elk记录如果量大的话就不适合全写到log中，如果量小都写入则有利于信息收集，
 * 这里通过配置文件和注解提供缺省配置，同时运行时可以按需动态改变
 */
@Getter
@Setter
public class DefaultElkSwitchManager implements IElkSwitchManager {
	private ZhiyoufyCommonProperties commonProperties;

	private boolean elkSwitchAllOn;
	private Set<String> elkSwitchOns = ConcurrentHashMap.newKeySet();

	@Autowired
	public DefaultElkSwitchManager(ZhiyoufyCommonProperties commonProperties) {
		this.commonProperties = commonProperties;

		this.elkSwitchAllOn = commonProperties.isElkSwitchAllOn();

		if (commonProperties.getElkSwitchOns() != null) {
			commonProperties.getElkSwitchOns().stream()
					.map(String::trim).forEach(elkSwitchOns::add);
		}
	}

	public boolean isElkSwitchOn(String type, String[] tags) {
		if (elkSwitchOns.contains(type)) {
			return true;
		}

		if (tags == null) {
			return false;
		}

		return Arrays.stream(tags).anyMatch(elkSwitchOns::contains);
	}
}
