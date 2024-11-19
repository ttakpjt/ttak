// package com.ttak.backend.global.auth.handler;
//
// import java.io.IOException;
//
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.web.access.AccessDeniedHandler;
//
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// public class CustomAccessDeniedHandler implements AccessDeniedHandler {
//
// 	@Override
// 	public void handle(HttpServletRequest request, HttpServletResponse response,
// 		AccessDeniedException accessDeniedException) throws IOException {
// 		log.error("AccessDeniedException is occurred. ", accessDeniedException);
// 		response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
// 	}
// }
