package com.ttak.backend.domain.observe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.observe.entity.BanList;

public interface BanListRepository extends JpaRepository<BanList, Long> {
}
