package com.ttak.backend.domain.observe.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.dto.CreateFriendRequest;
import com.ttak.backend.domain.observe.dto.StatusUpdateMessage;
import com.ttak.backend.domain.observe.service.FriendService;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;
	private final UserRepository userRepository;

	//상태 변경 알림
	// @PostMapping("/status")
	@PostMapping("/status/{userId}")
	// public ResponseEntity<?>  sendStatusChange(@RequestParam int state, @authUser User user) {
	public ResponseEntity<?>  sendStatusChange(@RequestParam int state, @PathVariable Long userId) {
		// StatusUpdateMessage message = StatusUpdateMessage.of(user.getUserId(), state);
		StatusUpdateMessage message = StatusUpdateMessage.of(userId, state);
		// 1. Redis에 상태 업데이트
		friendService.updateUserStatusInRedis(message.getUserId(), message.getStatus());
		// 2. 모든 클라이언트가 구독 중인 토픽으로 전송
		friendService.broadcastStatusUpdate(message);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// @PostMapping("/{followingId}")
	@PostMapping("/{followingId}/{userId}")
	// public ResponseEntity<?> createFriend(@PathVariable Long followingId, @authUser User user) {
	public ResponseEntity<?> createFriend(@PathVariable Long followingId, @PathVariable Long userId) {
		CreateFriendRequest createFriendRequest = CreateFriendRequest.of(userId, followingId);
			// CreateFriendRequest createFriendRequest = CreateFriendRequest.of(user.getUserId(), followingId);
		friendService.addFriend(createFriendRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// @DeleteMapping("/{followingId}")
	@DeleteMapping("/{followingId}/{userId}")
	// public ResponseEntity<?> removeFriend(@PathVariable Long followingId, @authUser User user) {
	public ResponseEntity<?> removeFriend(@PathVariable Long followingId, @PathVariable Long userId) {
		// friendService.deleteFriend(user.getUserId(), followingId);
		friendService.deleteFriend(userId, followingId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// @GetMapping("/live")
	@GetMapping("/live/{userId}")
	// public ResponseEntity<?> getLiveFriends(@authUser User user) {
	public ResponseEntity<?> getLiveFriends(@PathVariable Long userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
		return ResponseEntity.ok(friendService.getBannedFriends(user));
	}
}
