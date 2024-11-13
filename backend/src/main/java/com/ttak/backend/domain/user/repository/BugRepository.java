package com.ttak.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.user.entity.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {
}
