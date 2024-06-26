package com.example.zhiyoufy.server.service;

import java.util.List;

public interface EmsEnvironmentLockService {
	boolean tryLock(List<Long> envIdList, long timeoutMs);
	void unlock(List<Long> envIdList);
}
