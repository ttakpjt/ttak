package com.ttak.backend.domain.observe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.entity.CreateFriendRequest;
import com.ttak.backend.domain.observe.entity.StatusUpdateMessage;
import com.ttak.backend.domain.observe.service.FriendService;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.global.auth.annotation.authUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;

	//상태 변경 알림
	@PostMapping("/friends/status")
	public ResponseEntity<?>  sendStatusChange(@RequestParam int state, @authUser User user) {
		StatusUpdateMessage message = StatusUpdateMessage.of(user.getUserId(), state);
		// 1. Redis에 상태 업데이트
		friendService.updateUserStatusInRedis(message.getUserId(), message.getStatus());
		// 2. 모든 클라이언트가 구독 중인 토픽으로 전송
		friendService.broadcastStatusUpdate(message);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/friends/{followingId}")
	public ResponseEntity<?> createFriend(@PathVariable Long followingId, @authUser User user) {
		CreateFriendRequest createFriendRequest = CreateFriendRequest.of(user.getUserId(), followingId);
		friendService.addFriend(createFriendRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/friends/{followingId}")
	public ResponseEntity<?> removeFriend(@PathVariable Long followingId, @authUser User user) {
		friendService.deleteFriend(user.getUserId(), followingId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
