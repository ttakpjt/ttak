// package com.ttak.backend.global.auth.handler;
//
// import java.io.IOException;
//
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.AuthenticationEntryPoint;
//
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
// 	@Override
// 	public void commence(HttpServletRequest request, HttpServletResponse response,
// 		AuthenticationException authException) throws IOException {
// 		log.error("AuthenticationException is occurred. ", authException);
// 		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패하였습니다.");
// 	}
// }