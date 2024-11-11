package com.ttak.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//클라이언트가 구독한 url
		config.enableSimpleBroker("/topic");

		// 클라이언트가 메시지를 보낼 때 사용할 경로 접두사 설정
		config.setApplicationDestinationPrefixes("/app/status");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 웹소켓 연결 엔드포인트
		registry.addEndpoint("/api/wss", "/api/ws")
			.setAllowedOriginPatterns("https://k11a509.p.ssafy.io", "http://localhost:3000")
			.withSockJS();
	}
}