package com.ttak.backend.domain.observe.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttak.backend.domain.observe.dto.response.DeleteFriendsListResp;
import com.ttak.backend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.dto.reqeust.CreateFriendReq;
import com.ttak.backend.domain.observe.dto.StatusUpdateMessage;
import com.ttak.backend.domain.observe.service.FriendService;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.auth.annotation.UserPk;
import com.ttak.backend.global.common.CommonResponse;
import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

	private static final Logger log = LoggerFactory.getLogger(FriendController.class);
	private final FriendService friendService;
	private final UserRepository userRepository;
	private final UserService userService;

	//상태 변경 알림
	@PostMapping("/status")
	public ResponseEntity<CommonResponse<?>>  sendStatusChange(@RequestParam int state, @UserPk Long userId) {
		StatusUpdateMessage message = StatusUpdateMessage.of(userId, state);
		// 1. Redis에 상태 업데이트
		friendService.updateUserStatusInRedis(message.getUserId(), message.getStatus());
		// 2. 모든 클라이언트가 구독 중인 토픽으로 전송
		friendService.broadcastStatusUpdate(message);
		return ResponseEntity.ok(CommonResponse.success());
	}

	@PostMapping("/{followingId}")
	public ResponseEntity<CommonResponse<?>> createFriend(@PathVariable Long followingId, @UserPk Long userId) {
		CreateFriendReq createFriendReq = CreateFriendReq.of(userId, followingId);
		friendService.addFriend(createFriendReq);
		return ResponseEntity.ok(CommonResponse.success());
	}

	@DeleteMapping("/{followingId}")
	public ResponseEntity<CommonResponse<?>> removeFriend(@PathVariable Long followingId, @UserPk Long userId) {
		friendService.deleteFriend(userId, followingId);
		return ResponseEntity.ok(CommonResponse.success());
	}

	@GetMapping("/live")
	public ResponseEntity<CommonResponse<?>> getLiveFriends(@UserPk Long userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
		return ResponseEntity.ok(CommonResponse.success(friendService.getBannedFriends(user)));
	}

	@GetMapping("/follower")
	public ResponseEntity<CommonResponse<?>> getFollowerFriendsNum(@UserPk Long userId) {
		Long followerNum = friendService.getFollowerNum(userId);
		Map<String, Long> message = new HashMap<>();
		message.put("followerNum", followerNum);
		return ResponseEntity.ok(CommonResponse.success(message));
	}

//	@Operation(summary = "전체 친구 조회", description = "현재 내가 팔로우 하고 있는 친구 전체 목록을 반환한다.")
//	@GetMapping("/")
//	public ResponseEntity<CommonResponse<?>> getFriends(@UserPk final Long userId) {
//		log.info("========== 전체 친구목록 반환 시작 ==========");
//		List<DeleteFriendsListResp> list = userService.getFriendsList(userId);
//		log.info("========== 전체 친구목록 반환 종료 ==========");
//		return ResponseEntity.ok(CommonResponse.success(list));
//	}
}
