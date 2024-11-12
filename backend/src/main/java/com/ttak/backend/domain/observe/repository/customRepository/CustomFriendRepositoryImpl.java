package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.observe.dto.FriendInfoResponse;
import com.ttak.backend.domain.observe.dto.QFriendInfoResponse;
import com.ttak.backend.domain.observe.entity.QBanList;
import com.ttak.backend.domain.observe.entity.QFriend;
import com.ttak.backend.domain.user.entity.QUser;
import com.ttak.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFriendRepositoryImpl implements CustomFriendRepository{

	private final JPAQueryFactory jpaQueryFactory;

	// @Override
	// public List<FriendInfoResponse> findBannedFriendsByLocalTime(User user, LocalTime currentTime) {
	// 	QFriend friend = QFriend.friend;
	// 	QBanList banList = QBanList.banList;
	// 	QUser followingUser = QUser.user;
	//
	// 	return jpaQueryFactory
	// 		.select(new QFriendInfoResponse(
	// 			followingUser.nickname,
	// 			followingUser.userId,
	// 			followingUser.profilePic
	// 		))
	// 		.from(friend)
	// 		.join(friend.followingId, followingUser)
	// 		.join(banList).on(banList.user.eq(followingUser))
	// 		.where(friend.userId.eq(user)
	// 			.and(
	// 				// 자정을 넘지 않는 경우
	// 				(banList.startTime.loe(currentTime)
	// 					.and(banList.endTime.gt(currentTime)))
	// 					.or(
	// 						// 자정을 넘는 경우
	// 						banList.startTime.goe(banList.endTime)
	// 							.and(banList.startTime.loe(currentTime)
	// 								.or(banList.endTime.gt(currentTime)))
	// 					)
	// 			)
	// 		)
	// 		.fetch();
	// }

	@Override
	public List<FriendInfoResponse> findBannedFriendsByLocalDateTime(User user, LocalDateTime currentTime) {
		QFriend friend = QFriend.friend;
		QBanList banList = QBanList.banList;
		QUser followingUser = QUser.user;

		return jpaQueryFactory
			.select(new QFriendInfoResponse(
				followingUser.nickname,
				followingUser.userId,
				followingUser.profilePic
			))
			.from(friend)
			.join(friend.followingId, followingUser)
			.join(banList).on(banList.user.eq(followingUser))
			.where(friend.userId.eq(user)
				.and(Expressions.booleanTemplate(
					"{0} <= {1} and {1} < {2}",
					banList.startTime, currentTime, banList.endTime
				)))
			.fetch();
	}

	@Override
	public long countFollowers(Long userId) {
		QFriend friend = QFriend.friend;
		Long num = jpaQueryFactory
			.select(friend.count())
			.from(friend)
			.where(friend.followingId.userId.eq(userId))
			.fetchOne();

		return num != null ? num : 0L; // num이 null일 경우 0을 반환
	}

}
