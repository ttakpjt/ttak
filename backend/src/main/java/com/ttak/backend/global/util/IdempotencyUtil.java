package com.ttak.backend.global.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdempotencyUtil {
	private final RedisTemplate<String, String> redisTemplate;

	private static final long EXPIRATION_TIME = 60; // 1분
	/**
	 * 결제 요청 멱등성 보장
	 * @param : String key(메서드키-유저id-금액)
	 * @return True | False
	 */
	public boolean addRequest(String key) {
		return Boolean.TRUE.equals(
			redisTemplate.opsForValue().setIfAbsent(key, "success", EXPIRATION_TIME, TimeUnit.SECONDS));
	}
}
