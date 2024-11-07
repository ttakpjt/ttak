package com.ttak.backend.global.auth.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*30)     // 설정한 값을 Redis 의 key 값 prefix 로 사용한다. ttl 은 3일
public class RefreshToken {

	@Id     // key 값이 되며, jwtToken:{id} 위치에 auto-increment 된다.
	private String id;

	@Indexed        // 이 어노테이션이 있어야, 해당 필드 값으로 데이터를 찾아올 수 있다.
	private String accessToken;

	private String refreshToken;

}
