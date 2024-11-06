package com.ttak.backend.domain.observe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.entity.CreateFriendRequest;
import com.ttak.backend.domain.observe.entity.StatusUpdateMessage;
import com.ttak.backend.domain.observe.service.FriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;

	// 클라이언트가 "/app/status/change" 경로로 금지 행동을 보고하면 이 메서드가 실행됨
	@MessageMapping("/change")
	public void handleStatusChange(StatusUpdateMessage message) {
		friendService.broadcastStatusUpdate(message);
	}

	//상태 변경 알림
	@PostMapping("/friends/status")
	public ResponseEntity<?>  sendStatusChange(@RequestBody StatusUpdateMessage message) {
		// 1. Redis에 상태 업데이트
		friendService.updateUserStatusInRedis(message.getUserId(), message.getStatus());
		// 2. 모든 클라이언트가 구독 중인 토픽으로 전송
		friendService.broadcastStatusUpdate(message);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/friends")
	public ResponseEntity<?> createFriend(@RequestBody CreateFriendRequest createFriendRequest) {
		friendService.addFriend(createFriendRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/friends/{userId}/{followingId}")
	public ResponseEntity<?> removeFriend(@PathVariable Long followingId, @PathVariable Long userId) {
		friendService.deleteFriend(userId, followingId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
