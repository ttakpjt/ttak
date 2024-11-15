package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalDateTime;
import java.util.List;

import com.ttak.backend.domain.observe.dto.response.FriendInfoResp;
import com.ttak.backend.domain.user.entity.User;

public interface CustomFriendRepository {
	// List<FriendInfoResponse> findBannedFriendsByLocalTime(User user, LocalTime currentTime);
	List<FriendInfoResp> findBannedFriendsByLocalDateTime(User user, LocalDateTime currentTime);
	long countFollowers(Long userId);
}
