package com.ttak.backend.global.auth.dto;

import static com.ttak.backend.domain.user.entity.enumFolder.SocialDomain.*;
import static com.ttak.backend.global.common.ErrorCode.*;

import java.util.Map;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.enumFolder.Role;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;
import com.ttak.backend.global.exception.UnAuthorizedException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserInfo {

	private final String name;
	private final String profile;
	private final SocialDomain socialDomain;
	private final String socialIdentify;

	// registrationId에 따라 OAuth2UserInfo 객체를 생성하는 정적 팩토리 메서드
	public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
		return switch (registrationId) {
			case "google" -> ofGoogle(attributes);
			case "kakao" -> ofKakao(attributes);
			default -> throw new UnAuthorizedException(A002);
		};
	}

	// Google 사용자 정보 매핑 메서드
	private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
		return OAuth2UserInfo.builder()
			.name((String) attributes.get("name"))
			.profile((String) attributes.get("picture"))
			.socialDomain(GOOGLE)
			.socialIdentify((String) attributes.get(GOOGLE.getProviderCode()))
			.build();
	}

	// Kakao 사용자 정보 매핑 메서드
	private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
		Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) account.get("profile");

		return OAuth2UserInfo.builder()
			.name((String) profile.get("nickname"))
			.profile((String) profile.get("profile_image_url"))
			.socialDomain(KAKAO)
			.socialIdentify(attributes.get(KAKAO.getProviderCode()).toString())
			.build();
	}

	public User toEntity() {
		return User.builder()
			.nickname(name)
			.role(Role.USER)
			.socialDomain(socialDomain)
			.socialIdentify(socialIdentify)
			.build();
	}

}
