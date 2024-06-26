package com.example.zhiyoufy.server.service;

import java.util.Date;

public interface TimeService {
	Date currentDate();
	long currentTimeMillis();
	long nanoTime();
}
