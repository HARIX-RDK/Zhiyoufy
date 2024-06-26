package com.example.zhiyoufy.server.service.impl;

import java.util.Date;

import com.example.zhiyoufy.server.service.TimeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TimeServiceImpl implements TimeService {
	@Override
	public Date currentDate() {
		return new Date();
	}

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	@Override
	public long nanoTime() {
		return System.nanoTime();
	}
}
