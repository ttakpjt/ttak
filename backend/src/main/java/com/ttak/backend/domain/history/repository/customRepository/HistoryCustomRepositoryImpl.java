package com.ttak.backend.domain.history.repository.customRepository;

import static com.ttak.backend.domain.history.entity.QHistory.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ttak.backend.domain.history.dto.response.HistoryListRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HistoryCustomRepositoryImpl implements HistoryCustomRepository{

	private final JPAQueryFactory factory;

	@Override
	public List<HistoryListRes> findByReceiveId(Long userId) {
		return factory
			.select(Projections.constructor(HistoryListRes.class,
				history.type,
				history.message,
				history._super.createdAt))
			.from(history)
			.where(history.receiveId.eq(userId))
			.fetch();
	}
}
