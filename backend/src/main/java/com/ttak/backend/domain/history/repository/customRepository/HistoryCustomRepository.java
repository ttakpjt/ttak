package com.ttak.backend.domain.history.repository.customRepository;

import java.time.LocalDateTime;
import java.util.List;

import com.ttak.backend.domain.history.dto.response.HistoryListRes;

public interface HistoryCustomRepository {
	Long getTotalCount(LocalDateTime startOfWeek, LocalDateTime endOfToday);
	Long getMyCount(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfToday);
	Long getFriendsCount(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfToday);

	List<HistoryListRes> findByReceiveId(Long userId);
}
