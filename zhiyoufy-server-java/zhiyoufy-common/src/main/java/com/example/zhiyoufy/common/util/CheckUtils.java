package com.example.zhiyoufy.common.util;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CheckUtils {
	public static boolean WaitCondition(Supplier<Boolean> condition, int maxWaitMs, int intervalMs) {
		long endTime = System.currentTimeMillis() + maxWaitMs;

		while (System.currentTimeMillis() < endTime &&
				(endTime - System.currentTimeMillis()) <= maxWaitMs) {
			if (condition.get()) {
				return true;
			}

			try {
				Thread.sleep(intervalMs);
			}
			catch (InterruptedException e) {
				log.error("WaitCondition interrupted", e);
				return false;
			}
		}

		return false;
	}

	public static boolean isTrue(Boolean value) {
		return value != null && value;
	}
}
