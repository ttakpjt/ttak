package com.ttak.backend.global.auth.filter;

import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ttak.backend.global.common.TokenKey;
import com.ttak.backend.global.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = resolveToken(request);

		// accessToken 검증
		if (jwtUtil.validateToken(accessToken)) {
			Authentication authentication = jwtUtil.getAuthentication(accessToken);
			System.out.println("authentication.getPrincipal(): " + authentication.getPrincipal());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			setAuthentication(accessToken);
		} else {
			// 만료되었을 경우 accessToken 재발급
			String reissueAccessToken = jwtUtil.reissueAccessToken(accessToken, response);

			if (StringUtils.hasText(reissueAccessToken)) {
				setAuthentication(reissueAccessToken);

				Authentication authentication = jwtUtil.getAuthentication(reissueAccessToken);
				System.out.println("authentication.getPrincipal(): " + authentication.getPrincipal());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				// 재발급된 accessToken 다시 전달
				response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + reissueAccessToken);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String accessToken) {
		Authentication authentication = jwtUtil.getAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION);
		if (ObjectUtils.isEmpty(token) || !token.startsWith(TokenKey.TOKEN_PREFIX)) {
			return null;
		}
		return token.substring(TokenKey.TOKEN_PREFIX.length());
	}
}
