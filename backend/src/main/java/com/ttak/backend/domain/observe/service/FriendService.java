package com.ttak.backend.domain.observe.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttak.backend.domain.observe.dto.reqeust.CreateFriendReq;
import com.ttak.backend.domain.observe.entity.Friend;
import com.ttak.backend.domain.observe.dto.response.FriendInfoResp;
import com.ttak.backend.domain.observe.dto.StatusUpdateMessage;
import com.ttak.backend.domain.observe.repository.FriendRepository;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.exception.DuplicateException;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService{

	private final FriendRepository friendRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String USER_STATUS_KEY_PREFIX = "user:status:";

	public void broadcastStatusUpdate(StatusUpdateMessage message) {
		messagingTemplate.convertAndSend("/topic/friend-status", message);
	}

	@Transactional
	public void updateUserStatusInRedis(Long userId, int status) {
		String key = USER_STATUS_KEY_PREFIX + userId;
		redisTemplate.opsForValue().set(key, String.valueOf(status));
	}

	@Transactional
	public void addFriend(CreateFriendReq createFriendReq) {
		Long userId = createFriendReq.getUserId();
		Long followingId = createFriendReq.getFollowingId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(U001));
		User following = userRepository.findById(followingId)
			.orElseThrow(() -> new NotFoundException(U001));

		// 중복 검사
		if (friendRepository.existsByFollowingAndFollower(userId, followingId)) {
			throw new DuplicateException(F000);
		}

		Friend friend = CreateFriendReq.toFriend(user, following);
		friendRepository.save(friend);


	}

	@Transactional
	public void deleteFriend(Long userId, Long followingId) {
		friendRepository.deleteByUserId_UserIdAndFollowingId_UserId(userId, followingId);
	}

	public List<FriendInfoResp> getBannedFriends(User user){
		LocalDateTime currentTime = LocalDateTime.now();
		List<FriendInfoResp> bannedFriends = friendRepository.findBannedFriendsByLocalDateTime(user, currentTime);

		//Redis에서 상태 값을 가져와서 응답 객체에 설정
		for (FriendInfoResp friend : bannedFriends) {
			String redisKey = "user:status:" + friend.getFriendId(); // Redis에서 저장된 키 형식
			Integer status = Integer.parseInt((String)redisTemplate.opsForValue().get(redisKey)); // 상태 값 조회
			if (status != null) {
				friend.updateStatus(status); // 상태 값을 설정
			}
		}
		return bannedFriends;
	}

	public Long getFollowerNum(Long userId) {
		return friendRepository.countFollowers(userId);
	}
}
