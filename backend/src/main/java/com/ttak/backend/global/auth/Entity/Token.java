// package com.ttak.backend.global.auth.Entity;
//
// import org.springframework.data.annotation.Id;
// import org.springframework.data.redis.core.RedisHash;
// import org.springframework.data.redis.core.index.Indexed;
//
// import lombok.AllArgsConstructor;
// import lombok.Getter;
//
// @Getter
// @AllArgsConstructor
// @RedisHash(value = "id", timeToLive = 60 * 60 * 24 * 7)
// public class Token {
//
// 	@Id
// 	private Long id;
//
// 	private String refreshToken;
//
// 	private String accessToken;
//
// 	public Token updateRefreshToken(String refreshToken) {
// 		this.refreshToken = refreshToken;
// 		return this;
// 	}
//
// 	public void updateAccessToken(String accessToken) {
// 		this.accessToken = accessToken;
// 	}
// }
