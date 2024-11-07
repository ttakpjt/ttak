package com.ttak.backend.domain.observe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.observe.entity.BanApp;

public interface BanAppRepository extends JpaRepository<BanApp, Long> {
}
