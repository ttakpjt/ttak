package com.ttak.backend.global.auth.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttak.backend.global.auth.Entity.Token;
import com.ttak.backend.global.auth.repository.TokenRepository;
import com.ttak.backend.global.exception.UnAuthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService{

	private final TokenRepository tokenRepository;

	@Override
	public void deleteRefreshToken(String memberKey) {
		tokenRepository.deleteById(memberKey);
	}

	@Override
	@Transactional
	public void saveOrUpdate(Long userId, String refreshToken, String accessToken) {
		Token token = tokenRepository.findByAccessToken(accessToken)
			.map(o -> o.updateRefreshToken(refreshToken))
			.orElseGet(() -> new Token(userId, refreshToken, accessToken));

		tokenRepository.save(token);
	}

	@Override
	public Token findByAccessTokenOrThrow(String accessToken) {
		return tokenRepository.findByAccessToken(accessToken)
			.orElseThrow(() -> new UnAuthorizedException(A003));
	}

	@Override
	@Transactional
	public void updateToken(String accessToken, Token token) {
		token.updateAccessToken(accessToken);
		tokenRepository.save(token);
	}
}
