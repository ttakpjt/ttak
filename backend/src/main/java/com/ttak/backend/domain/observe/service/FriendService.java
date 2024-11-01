package com.ttak.backend.domain.observe.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import com.ttak.backend.domain.observe.entity.StatusUpdateMessage;
import com.ttak.backend.domain.observe.repository.FriendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService{

	private final FriendRepository friendRepository;
	private final SimpMessagingTemplate messagingTemplate;


	// 상태 업데이트 브로드캐스트
	public void broadcastStatusUpdate(StatusUpdateMessage message) {

		// 모든 클라이언트가 구독 중인 토픽으로 전송
		messagingTemplate.convertAndSend("/topic/friend-status", message);
	}
}
