package com.example.zhiyoufy.server.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.example.zhiyoufy.server.service.EmsEnvironmentLockService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@Slf4j
public class EmsEnvironmentLockServiceImpl implements EmsEnvironmentLockService {
	ReentrantLock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	Set<Long> lockedEnvIdSet = new HashSet<>();

	@Override
	public boolean tryLock(List<Long> envIdList, long timeoutMs) {
		long endNanos = System.nanoTime() + timeoutMs * 1_000_000;
		boolean anyLocked;
		long leftNanos;

		lock.lock();
		try {
			while (true) {
					anyLocked = false;
					for (Long envId : envIdList) {
						if (lockedEnvIdSet.contains(envId)) {
							anyLocked = true;
							break;
						}
					}

					if (!anyLocked) {
						lockedEnvIdSet.addAll(envIdList);
						return true;
					}

					leftNanos = endNanos - System.nanoTime();

					if (leftNanos <= 0) {
						return false;
					}

					condition.awaitNanos(leftNanos);
				}
			}
		catch (InterruptedException e) {
			log.error("await interrupted", e);
		}
		finally {
			lock.unlock();
		}

		return false;
	}

	@Override
	public void unlock(List<Long> envIdList) {
		lock.lock();

		lockedEnvIdSet.removeAll(envIdList);
		condition.signalAll();

		lock.unlock();
	}
}
