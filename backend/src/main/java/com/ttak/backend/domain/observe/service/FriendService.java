package com.ttak.backend.domain.observe.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttak.backend.domain.observe.entity.CreateFriendRequest;
import com.ttak.backend.domain.observe.entity.Friend;
import com.ttak.backend.domain.observe.entity.StatusUpdateMessage;
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

	// 상태 업데이트 브로드캐스트
	public void broadcastStatusUpdate(StatusUpdateMessage message) {

		// 모든 클라이언트가 구독 중인 토픽으로 전송
		messagingTemplate.convertAndSend("/topic/friend-status", message);
	}

	@Transactional
	public void addFriend(CreateFriendRequest createFriendRequest) {
		Long userId = createFriendRequest.getUserId();
		Long followingId = createFriendRequest.getFollowingId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(U001));
		User following = userRepository.findById(followingId)
			.orElseThrow(() -> new NotFoundException(U001));

		// 중복 검사
		if (friendRepository.existsByFollowingAndFollower(userId, followingId)) {
			throw new DuplicateException(F000);
		}

		Friend friend = CreateFriendRequest.toFriend(user, following);
		friendRepository.save(friend);


	}

	@Transactional
	public void deleteFriend(Long userId, Long followingId) {
		friendRepository.deleteByUserId_UserIdAndFollowingId_UserId(userId, followingId);
	}

}
