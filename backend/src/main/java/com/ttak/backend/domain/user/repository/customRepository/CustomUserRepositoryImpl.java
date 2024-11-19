package com.ttak.backend.domain.user.repository.customRepository;

import static com.ttak.backend.domain.observe.entity.QFriend.*;
import static com.ttak.backend.domain.user.entity.QUser.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.observe.dto.response.DeleteFriendsListResp;
import com.ttak.backend.domain.observe.entity.QFriend;
import com.ttak.backend.domain.user.dto.response.UserInfoResponse;
import com.ttak.backend.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<UserInfoResponse> findUsersWithRelation(Long currentUserId, String nickname) {

		return jpaQueryFactory
			.select(Projections.constructor(
				UserInfoResponse.class,
				user.nickname,
				user.userId,
				user.profilePic,
				new CaseBuilder()
					.when(user.userId.eq(currentUserId)).then("Self")
					.when(friend.userId.userId.eq(currentUserId).and(friend.followingId.userId.eq(user.userId)))
					.then("Friend")
					.otherwise("None")
			))
			.from(user)
			.leftJoin(friend).on(friend.followingId.userId.eq(user.userId).and(friend.userId.userId.eq(currentUserId)))
			.where(user.nickname.containsIgnoreCase(nickname))
			.fetch();
	}

	@Override
	public String findProfilePicByUserId(Long userId) {
		return jpaQueryFactory
			.select(user.profilePic)
			.from(user)
			.where(user.userId.eq(userId).and(user.deleteYn.eq(false)))
			.fetchOne();
	}
}
