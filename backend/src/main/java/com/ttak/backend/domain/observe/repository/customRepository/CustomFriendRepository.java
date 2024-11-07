package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalTime;
import java.util.List;

import com.ttak.backend.domain.observe.dto.FriendInfoResponse;
import com.ttak.backend.domain.user.entity.User;

public interface CustomFriendRepository {
	List<FriendInfoResponse> findBannedFriends(User user, LocalTime currentTime);
	long countFollowers(Long userId);
}
