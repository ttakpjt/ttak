package com.ttak.backend.domain.observe.repository.customRepository;

import java.time.LocalTime;
import java.util.List;

import com.ttak.backend.domain.observe.entity.FriendInfoResponse;
import com.ttak.backend.domain.user.entity.User;

public interface CustomFriendRepository {
	List<FriendInfoResponse> findBannedFriends(User user, LocalTime currentTime);

}
