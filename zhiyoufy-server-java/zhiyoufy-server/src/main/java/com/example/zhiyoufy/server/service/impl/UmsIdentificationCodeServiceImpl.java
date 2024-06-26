package com.example.zhiyoufy.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.server.service.UmsIdentificationCodeService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 用户标识码Service实现类
 */
@Service
@Getter
@Setter
@Slf4j
public class UmsIdentificationCodeServiceImpl implements UmsIdentificationCodeService {
	private final int codeLen = 8;
	private final long expireAfterInterval = 600L * 1000_000_000;
	private final long minRegenerateInterval = 60L * 1000_000_000;
	private final long minCheckInterval = 10L * 1000_000_000;
	private final int maxLiveCode = 10;

	private Map<String, Long> codeExpireTimeMap = new HashMap<>();
	private Map<String, Long> codeCreateTimeMap = new HashMap<>();
	private Map<String, String> idKeyToCodeMap = new HashMap<>();
	private long lastCheckExpireTime = -1;

	private Supplier<Long> timeSupplier;

	/**
	 * 生成标识码
	 *
	 * 会限制调用频率进而控制发送标识码的频率
	 */
	@Override
	public synchronized String generateIdentificationCode(String idKey) {
		expireIfNeeded();

		long curTime = getCurrentTime();

		// 检查是否调用太频繁
		if (idKeyToCodeMap.containsKey(idKey)) {
			long createTime = codeCreateTimeMap.get(idKey);

			if (curTime < (createTime + minRegenerateInterval)) {
				Asserts.fail(CommonErrorCode.RES_TOO_FREQUENT);
			}

			removeItem(idKey);
		}

		if (idKeyToCodeMap.size() >= maxLiveCode) {
			Asserts.fail(CommonErrorCode.RES_OVER_CAPACITY
					.toCustomDetail("超过最高活跃标识码上限"));
		}

		String code = generateRandomCode();

		idKeyToCodeMap.put(idKey, code);
		codeCreateTimeMap.put(idKey, curTime);
		codeExpireTimeMap.put(idKey, curTime + expireAfterInterval);

		log.info("generated code for {}", idKey);

		return code;
	}

	@Override
	public synchronized String getIdentificationCode(String idKey) {
		expireIfNeeded();

		return idKeyToCodeMap.get(idKey);
	}

	@Override
	public synchronized void removeIdentificationCode(String idKey) {
		removeItem(idKey);
	}

	private void expireIfNeeded() {
		long curTime = getCurrentTime();

		if ((curTime - lastCheckExpireTime) < minCheckInterval) {
			return;
		}

		lastCheckExpireTime = curTime;

		List<String> idKeyList = new ArrayList<>(idKeyToCodeMap.keySet());

		for (String idKey : idKeyList) {
			Long expireTime = codeExpireTimeMap.get(idKey);

			if (curTime >= expireTime) {
				removeItem(idKey);

				log.debug("expire code for idKey {}", idKey);
			}
		}
	}

	private void removeItem(String idKey) {
		idKeyToCodeMap.remove(idKey);
		codeCreateTimeMap.remove(idKey);
		codeExpireTimeMap.remove(idKey);
	}

	private String generateRandomCode() {
		StringBuilder codeBuilder = new StringBuilder();

		for (int i = 0; i < codeLen; i++) {
			int digit = RandomUtils.randomInt(0, 10);
			codeBuilder.append(digit);
		}

		return codeBuilder.toString();
	}

	private long getCurrentTime() {
		if (timeSupplier == null) {
			return System.nanoTime();
		} else {
			return timeSupplier.get();
		}
	}
}
