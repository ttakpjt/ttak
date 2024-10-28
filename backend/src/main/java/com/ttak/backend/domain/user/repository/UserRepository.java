package com.ttak.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
