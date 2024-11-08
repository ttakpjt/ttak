package com.ttak.backend.domain.history.repository.customRepository;

import java.util.List;

import com.ttak.backend.domain.history.dto.response.HistoryListRes;

public interface HistoryCustomRepository {

	List<HistoryListRes> findByReceiveId(Long userId);
}
