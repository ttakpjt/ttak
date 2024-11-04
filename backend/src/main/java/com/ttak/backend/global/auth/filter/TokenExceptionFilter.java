package com.ttak.backend.global.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.UnAuthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (UnAuthorizedException e) {
			throw new UnAuthorizedException(ErrorCode.A002);
		}
	}
}
