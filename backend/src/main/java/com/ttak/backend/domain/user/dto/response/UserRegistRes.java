package com.ttak.backend.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserRegistRes {
	private Long userId;

	public static UserRegistRes toEntity(Long userId) {
		return UserRegistRes.builder()
			.userId(userId)
			.build();
	}

}
