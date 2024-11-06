package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.observe.entity.FriendInfoResponse;
import com.ttak.backend.domain.observe.entity.QBanList;
import com.ttak.backend.domain.observe.entity.QFriend;
import com.ttak.backend.domain.observe.entity.QFriendInfoResponse;
import com.ttak.backend.domain.user.entity.QUser;
import com.ttak.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFriendRepositoryImpl implements CustomFriendRepository{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<FriendInfoResponse> findBannedFriends(User user, LocalTime currentTime) {
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
				.and(banList.startTime.loe(currentTime))
				.and(banList.endTime.gt(currentTime)))
			.fetch();
	}

}
