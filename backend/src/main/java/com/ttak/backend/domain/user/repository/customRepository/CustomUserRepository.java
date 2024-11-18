package com.ttak.backend.domain.user.repository.customRepository;

import java.util.List;

import com.ttak.backend.domain.user.dto.response.UserInfoResponse;

public interface CustomUserRepository {
	List<UserInfoResponse> findUsersWithRelation(Long currentUserId, String nickname);

	String findProfilePicByUserId(Long userId);
}
