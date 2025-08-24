package io.hhplus.tdd.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class LockManager {
	private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

	public ReentrantLock getLock(Long userId) {
		return locks.computeIfAbsent(userId, id -> new ReentrantLock(true));
	}
}
