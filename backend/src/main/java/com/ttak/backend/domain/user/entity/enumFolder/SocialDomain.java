package com.ttak.backend.domain.user.entity.enumFolder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialDomain {
	GITHUB("id"),
	KAKAO("id"),
	NAVER("id"),
	GOOGLE("sub");

	private final String providerCode;;
}

