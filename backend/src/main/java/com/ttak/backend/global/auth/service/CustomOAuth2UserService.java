package com.ttak.backend.global.auth.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.auth.dto.OAuth2UserInfo;
import com.ttak.backend.global.auth.dto.UserPrincipal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);
		System.out.println(userRequest.getClientRegistration());
		System.out.println(oAuth2User.getAttributes());

		// 1. 유저 정보(attributes) 가져오기
		Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

		// 2. 서비스를 구분하는 코드 ex) Github, Naver
		String registrationId  = userRequest.getClientRegistration().getRegistrationId();

		// 3. 각 도메인별 attribute에 접근하기위한 식별자를 가져온다. (kakao는 "id", google은 "sub")
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		// 4. 유저정보에 대한 DTO를 생성한다.
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

		// 5. 회원가입 및 로그인
		User user = getUser(oAuth2UserInfo);

		// Security context에 저장할 객체 생성
		return new UserPrincipal(user, oAuth2UserAttributes, userNameAttributeName);
	}

	private User getUser(OAuth2UserInfo oAuth2UserInfo) {
		User user = userRepository.findBySocialDomainAndSocialIdentify(oAuth2UserInfo.getSocialDomain(), oAuth2UserInfo.getSocialIdentify())
			.orElseGet(() -> {
				User newUser = oAuth2UserInfo.toEntity();
				return userRepository.save(newUser);
			});
		System.out.println("userId: " + user.getUserId());
		return user;
	}
}
