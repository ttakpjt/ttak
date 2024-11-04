package com.ttak.backend.domain.user.repository.customRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

	private final JPAQueryFactory jpaQueryFactory;


}
