package com.ttak.backend.domain.history.repository.customRepository;

import static com.ttak.backend.domain.history.entity.QHistory.*;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.history.dto.response.HistoryListRes;
import com.ttak.backend.domain.history.entity.QHistory;
import com.ttak.backend.domain.observe.entity.QFriend;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HistoryCustomRepositoryImpl implements HistoryCustomRepository{
	private final JPAQueryFactory jpaQueryFactory;


	@Override
	public Long getTotalCount(LocalDateTime startOfWeek, LocalDateTime endOfToday) {
		QHistory history = QHistory.history;
		return jpaQueryFactory
			.select(history.count())
			.from(history)
			.where(history.createdAt.between(startOfWeek, endOfToday))
			.fetchOne();
	}

	@Override
	public Long getMyCount(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfToday) {
		QHistory history = QHistory.history;
		return jpaQueryFactory
			.select(history.count())
			.from(history)
			.where(
				history.receiveId.eq(userId),
				history.createdAt.between(startOfWeek, endOfToday)
			)
			.fetchOne();
	}

	@Override
	public Long getFriendsCount(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfToday) {
		QHistory history = QHistory.history;
		QFriend friend = QFriend.friend;
		return jpaQueryFactory
			.select(history.count())
			.from(history)
			.join(friend)
			.on(friend.followingId.userId.eq(history.receiveId))  // 친구 관계 조인
			.where(
				friend.userId.userId.eq(userId),  // 로그인된 사용자의 친구들만 필터링
				history.createdAt.between(startOfWeek, endOfToday)
			)
			.fetchOne();
	}

	@Override
	public List<HistoryListRes> findByReceiveId(Long userId) {
		return jpaQueryFactory
			.select(Projections.constructor(HistoryListRes.class,
				history.type,
				history.message,
				history._super.createdAt))
			.from(history)
			.where(history.receiveId.eq(userId))
			.fetch();
	}

}
