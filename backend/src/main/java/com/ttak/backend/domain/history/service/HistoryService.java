package com.ttak.backend.domain.history.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ttak.backend.domain.history.dto.request.HistoryCount;
import com.ttak.backend.domain.history.dto.response.HistoryListRes;
import com.ttak.backend.domain.history.entity.History;
import com.ttak.backend.domain.history.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;

	public HistoryCount getCumulativeCounts(Long userId) {
		LocalDateTime startOfWeek = getStartOfWeek();
		LocalDateTime endOfToday = getEndOfToday();

		// 전체 사용자 누적 횟수
		Long totalCount = historyRepository.getTotalCount(startOfWeek, endOfToday);
		// 나의 누적 횟수
		Long myCount = historyRepository.getMyCount(userId, startOfWeek, endOfToday);
		// 내 친구들의 누적 횟수
		Long friendsCount = historyRepository.getFriendsCount(userId, startOfWeek, endOfToday);

		//DTO 반환
		return HistoryCount.of(totalCount, myCount, friendsCount);
	}

	public LocalDateTime getStartOfWeek() {
		// 이번 주 일요일의 시작 시간 (일요일 00:00)
		return LocalDateTime.now()
			.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			.with(LocalTime.MIN);  // 00:00:00
	}

	public LocalDateTime getEndOfToday() {
		// 오늘의 마지막 시간 (23:59:59)
		return LocalDateTime.now()
			.with(LocalTime.MAX);  // 23:59:59
	}

	public List<HistoryListRes> getAttackHistory(Long userId){
		return historyRepository.findByReceiveId(userId);
	}

	public Long getUserPick(Long userId) {
		LocalDateTime startOfWeek = getStartOfWeek();
		LocalDateTime endOfToday = getEndOfToday();

		Long myCount = historyRepository.getMyCount(userId, startOfWeek, endOfToday);

		return myCount;
	}

	/**
	 * 전달된 fcmMessage를 Histroy 테이블에 저장한다.
	 * @param sendId 보내는 사람 Id (AttackId)
	 * @param data 데이터 내용
	 * @param receiveId 받는 사람 ID
	 * @param type 데이터 타입 (메세지, 물풍선, ...)
	 */
	public void addAttackHistory(final Long sendId, final String data, final Long receiveId, final String type){
		historyRepository.save(History.of(sendId, receiveId, type, data));
	}
}
