package com.ttak.backend.global.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdempotencyUtil {
	private final RedisTemplate<String, String> redisTemplate;

	private static final long HISTORY_EXPIRATION_TIME = 60; // 1분
	private static final long FCM_EXPIRATION_TIME = 2; //2초
	/**
	 * 결제 요청 멱등성 보장
	 * @param : String key(메서드키-유저id-메세지)
	 * @return True | False
	 */
	public boolean addHistoryRequest(String key) {
		return Boolean.TRUE.equals(
			redisTemplate.opsForValue().setIfAbsent(key, "success", HISTORY_EXPIRATION_TIME, TimeUnit.SECONDS));
	}

	/**
	 * 결제 요청 멱등성 보장
	 * @param : String key(메서드키-유저id)
	 * @return True | False
	 */
	public boolean addFcmRequest(String key) {
		return Boolean.TRUE.equals(
				redisTemplate.opsForValue().setIfAbsent(key, "success", FCM_EXPIRATION_TIME, TimeUnit.SECONDS));
	}
}
