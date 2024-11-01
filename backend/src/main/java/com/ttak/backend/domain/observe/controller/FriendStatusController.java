package com.ttak.backend.domain.observe.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.entity.StatusUpdateMessage;
import com.ttak.backend.domain.observe.service.FriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FriendStatusController {

	private final FriendService friendService;

	// 클라이언트가 "/app/status/change" 경로로 금지 행동을 보고하면 이 메서드가 실행됨
	@MessageMapping("/change")
	public void handleStatusChange(StatusUpdateMessage message) {
		friendService.broadcastStatusUpdate(message);
	}

}
