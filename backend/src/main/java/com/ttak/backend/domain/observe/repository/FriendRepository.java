package com.ttak.backend.domain.observe.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.observe.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {

}