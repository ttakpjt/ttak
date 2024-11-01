package com.ttak.backend.global.auth.service;

import com.ttak.backend.global.auth.Entity.Token;

public interface TokenService {

	void deleteRefreshToken(String memberKey);

	void saveOrUpdate(String memberKey, String refreshToken, String accessToken);

	Token findByAccessTokenOrThrow(String accessToken);

	void updateToken(String accessToken, Token token);
}
