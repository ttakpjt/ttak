package com.ttak.backend.domain.user.entity;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderInfo {
	GITHUB(null, "id", "login"),
	KAKAO("kakao_account", "id", "properties"),
	NAVER("response", "id", "email"),
	GOOGLE(null, "sub", "email");

	private final String attributeKey;
	private final String providerCode;
	private final String identifier;

	public static ProviderInfo from(String provider) {
		String upperCastedProvider = provider.toUpperCase(); //ex) google > GOOGLE

		return Arrays.stream(ProviderInfo.values())
			.filter(item -> item.name().equals(upperCastedProvider))
			.findFirst()
			.orElseThrow();
	}
}
