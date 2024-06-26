package com.example.zhiyoufy.common.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zhiyoufy.common")
@Getter
@Setter
public class ZhiyoufyCommonProperties {
	// elk related
	private boolean elkSwitchAllOn = false;
	private List<String> elkSwitchOns;
}
