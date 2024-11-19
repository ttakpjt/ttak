package com.ttak.backend.domain.observe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.observe.entity.BanApp;
import com.ttak.backend.domain.observe.entity.BanList;

public interface BanAppRepository extends JpaRepository<BanApp, Long> {

	void deleteAllByBanList(BanList banList);
}
