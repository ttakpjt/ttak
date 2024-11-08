package com.ttak.backend.domain.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.history.entity.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
