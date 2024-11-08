package com.ttak.backend.domain.user.repository.customRepository;

import java.util.List;

import com.ttak.backend.domain.user.dto.response.UserInfoResponse;

public interface CustomUserRepository {
	public List<UserInfoResponse> findUsersWithRelation(Long currentUserId, String nickname);
}
