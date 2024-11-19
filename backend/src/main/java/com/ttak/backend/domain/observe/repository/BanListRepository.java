package com.ttak.backend.domain.observe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.observe.entity.BanList;
import com.ttak.backend.domain.user.entity.User;

public interface BanListRepository extends JpaRepository<BanList, Long> {

	Optional<BanList> findBanListByUser(User user);
}
