package com.ttak.backend.domain.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.history.entity.History;
import com.ttak.backend.domain.history.repository.customRepository.HistoryCustomRepository;

public interface HistoryRepository extends JpaRepository<History, Long>, HistoryCustomRepository {
}
