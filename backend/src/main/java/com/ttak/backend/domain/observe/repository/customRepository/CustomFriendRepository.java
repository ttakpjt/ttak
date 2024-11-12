package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.ttak.backend.domain.observe.dto.FriendInfoResponse;
import com.ttak.backend.domain.user.entity.User;

public interface CustomFriendRepository {
	List<FriendInfoResponse> findBannedFriendsByLocalTime(User user, LocalTime currentTime);
	List<FriendInfoResponse> findBannedFriendsByLocalDateTime(User user, LocalDateTime currentTime);
	long countFollowers(Long userId);
}
