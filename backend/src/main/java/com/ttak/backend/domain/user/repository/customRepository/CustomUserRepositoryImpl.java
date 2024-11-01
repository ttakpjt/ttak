package com.ttak.backend.domain.user.repository.customRepository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.user.entity.ProviderInfo;
import static com.ttak.backend.domain.user.entity.QUser.*;
import com.ttak.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<User> findOauthLoginUser(ProviderInfo providerInfo, String socialIdentify) {

		return Optional.empty();
	}
}
