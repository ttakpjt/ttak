package com.ttak.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.ttak.backend.global.auth.config.CustomCorsConfigurationSource;
import com.ttak.backend.global.auth.handler.OAuth2FailureHandler;
import com.ttak.backend.global.auth.handler.OAuth2SuccessHandler;
import com.ttak.backend.global.auth.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@Order(1)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final CustomCorsConfigurationSource customCorsConfigurationSource;
	private final CustomOAuth2UserService customOAuthService;
	private final OAuth2SuccessHandler successHandler;
	private final OAuth2FailureHandler failureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(corsCustomizer -> corsCustomizer
				.configurationSource(customCorsConfigurationSource)
			)
			// .csrf(CsrfConfigurer::disable)
			// .httpBasic(HttpBasicConfigurer::disable)
			// // OAuth 사용으로 인해 기본 로그인 비활성화
			// .formLogin(FormLoginConfigurer::disable)
			// .authorizeHttpRequests(request -> request
			// 	.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			// 	.anyRequest().hasAnyRole(PERMITTED_ROLES))

			// OAuth 로그인 설정
			.oauth2Login(customConfigurer -> customConfigurer
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuthService))
			);

		return http.build();
	}
}
