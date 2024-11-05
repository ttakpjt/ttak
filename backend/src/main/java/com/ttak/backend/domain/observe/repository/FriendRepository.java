package com.ttak.backend.domain.observe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ttak.backend.domain.observe.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	void deleteByUserId_UserIdAndFollowingId_UserId(Long userId, Long followingId);

	@Query("SELECT COUNT(f) > 0 FROM Friend f WHERE f.userId.userId = :userId AND f.followingId.userId = :followingId")
	boolean existsByFollowingAndFollower(@Param("userId") Long userId, @Param("followingId") Long followingId);
}