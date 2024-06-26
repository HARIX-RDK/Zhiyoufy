package com.example.zhiyoufy.common.util;

/**
 * 随机值工具类
 */
public class RandomUtils {
	public static long nextId() {
		long nextId = randomLong();
		while (nextId == 0L) {
			nextId = randomLong();
		}
		return nextId;
	}

	public static long nextTraceIdHigh() {
		return nextTraceIdHigh(java.util.concurrent.ThreadLocalRandom.current().nextInt());
	}

	static long nextTraceIdHigh(int random) {
		long epochSeconds = System.currentTimeMillis() / 1000;
		return (epochSeconds & 0xffffffffL) << 32
				| (random & 0xffffffffL);
	}

	public static long randomLong() {
		return java.util.concurrent.ThreadLocalRandom.current().nextLong();
	}

	public static int randomInt() {
		int nextInt = 0;

		while (nextInt == 0) {
			nextInt = java.util.concurrent.ThreadLocalRandom.current().nextInt();
		}

		return nextInt;
	}

	// max: exclusive
	public static int randomInt(int min, int max) {
		int nextInt = 0;

		nextInt = java.util.concurrent.ThreadLocalRandom.current()
				.nextInt(max - min) + min;

		return nextInt;
	}

	public static String generateShortHexId() {
		long traceIdHigh = nextTraceIdHigh();

		return HexUtils.toLowerHex(traceIdHigh);
	}

	public static String generateHexId() {
		long spanId = nextId();
		long traceIdHigh = nextTraceIdHigh();

		return HexUtils.toLowerHex(traceIdHigh, spanId);
	}
}
