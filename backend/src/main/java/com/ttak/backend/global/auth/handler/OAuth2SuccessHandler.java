package com.ttak.backend.global.auth.handler;

import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ttak.backend.global.common.TokenKey;
import com.ttak.backend.global.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	@Value("${spring.login.target-uri}")
	private String targetUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		log.info("===========OAuth2 로그인 성공==============");

		String accessToken = jwtUtil.generateAccessToken(authentication);
		System.out.println("accessToken: " + accessToken);
		jwtUtil.generateRefreshToken(authentication, accessToken, response);

		String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
			.build().toUriString();

		//response
		response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + accessToken);
		response.sendRedirect(redirectUrl);
	}
}
