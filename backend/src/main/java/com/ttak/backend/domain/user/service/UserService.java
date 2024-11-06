package com.ttak.backend.domain.user.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.UserInfoResponse;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.exception.NotFoundException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String USER_STATUS_KEY_PREFIX = "user:status:";

	public Long getUserId(Long id) {
		if(id >= 10) throw new NotFoundException(T000);
		return id;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<UserInfoResponse> searchUsersByNickname(String nickname) {
		return userRepository.findByNicknameContainingIgnoreCase(nickname)
			.stream()
			.map(user -> new UserInfoResponse(
				user.getNickname(),
				user.getUserId(),
				user.getProfilePic()
			))
			.collect(Collectors.toList());
	}

	@PostConstruct
	public void initializeAllUsers() {
		// 모든 사용자 ID를 가져와 초기화
		List<User> allUserIds = getAllUsers();
		for (User userId : allUserIds) {
			setUserStatus(userId.getUserId(), 1, 86400); // 24시간 TTL 설정
		}
	}


	private void setUserStatus(Long userId, int status, long ttlInSeconds) {
		// Redis에 상태 저장 로직
		redisTemplate.opsForValue().set("user:status:" + userId, String.valueOf(status), Duration.ofSeconds(ttlInSeconds));
	}


}
