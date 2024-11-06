// package com.ttak.backend.global.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
// import com.ttak.backend.global.auth.config.CustomCorsConfigurationSource;
// import com.ttak.backend.global.auth.filter.TokenAuthenticationFilter;
// import com.ttak.backend.global.auth.filter.TokenExceptionFilter;
// import com.ttak.backend.global.auth.handler.CustomAccessDeniedHandler;
// import com.ttak.backend.global.auth.handler.CustomAuthenticationEntryPoint;
// import com.ttak.backend.global.auth.handler.OAuth2FailureHandler;
// import com.ttak.backend.global.auth.handler.OAuth2SuccessHandler;
// import com.ttak.backend.global.auth.service.CustomOAuth2UserService;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// public class SecurityConfig {
//
// 	private final CustomCorsConfigurationSource customCorsConfigurationSource;
// 	private final TokenAuthenticationFilter tokenAuthenticationFilter;
// 	private final CustomOAuth2UserService customOAuthService;
// 	private final OAuth2SuccessHandler successHandler;
//
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
// 		http.cors(corsCustomizer -> corsCustomizer
// 				.configurationSource(customCorsConfigurationSource)
// 			)
// 			.csrf(AbstractHttpConfigurer::disable)
// 			.cors(AbstractHttpConfigurer::disable)
// 			.httpBasic(AbstractHttpConfigurer::disable)
// 			// OAuth 사용으로 인해 기본 로그인 비활성화
// 			.formLogin(AbstractHttpConfigurer::disable)
// 			.logout(AbstractHttpConfigurer::disable)
// 			.headers(c -> c.frameOptions(
// 				HeadersConfigurer.FrameOptionsConfig::disable).disable())
// 			.sessionManagement(c ->
// 				c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
// 			.authorizeHttpRequests((auth) -> auth
// 				.anyRequest().permitAll())
//
// 			// OAuth 로그인 설정
// 			.oauth2Login(customConfigurer -> customConfigurer
// 				.successHandler(successHandler)
// 				.failureHandler(new OAuth2FailureHandler())
// 				.userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuthService))
// 			)
//
// 			.addFilterBefore(tokenAuthenticationFilter,
// 				UsernamePasswordAuthenticationFilter.class)
// 			.addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass());
//
// 			// .exceptionHandling((exceptions) -> exceptions
// 			// 	.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
// 			// 	.accessDeniedHandler(new CustomAccessDeniedHandler()));
//
//
//
// 		return http.build();
// 	}
// }
