package com.ttak.backend.global.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

	// 세션 연결 이벤트
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		log.info("새로운 WebSocket 세션 연결 - 세션 ID: {}", sessionId);
	}

	// 세션 연결 종료 이벤트
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		log.info("WebSocket 세션 연결 종료 - 세션 ID: {}", sessionId);
	}

	@EventListener
	public void handleSubscriptionEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		// 구독한 경로와 세션 ID 확인
		if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
			String sessionId = headerAccessor.getSessionId();
			String destination = headerAccessor.getDestination();

			log.info("New Subscription - Session ID: {}, Destination: {}", sessionId, destination);
		}
	}
}
