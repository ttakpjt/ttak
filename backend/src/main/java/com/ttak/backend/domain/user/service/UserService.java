package com.ttak.backend.domain.user.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ttak.backend.domain.user.dto.reqeust.GoogleUserRequest;
import com.ttak.backend.domain.user.dto.response.UserInfoResponse;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.NotFoundException;
import com.ttak.backend.global.exception.UnAuthorizedException;

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
		// if(id >= 10) throw new NotFoundException(T000);
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
	@Scheduled(cron = "0 0 0 * * *") // 매일 정오 00시 00분에 실행
	public void initializeAllUsers() {
		// 모든 사용자 ID를 가져와 초기화
		List<Long> allUserIds = userRepository.findAllUserIds();
		for (Long userId : allUserIds) {
			//기본값은 0:green
			setUserStatus(userId, 0, 86400); // 24시간 TTL 설정
		}
	}


	private void setUserStatus(Long userId, int status, long ttlInSeconds) {
		// Redis에 상태 저장 로직
		redisTemplate.opsForValue().set("user:status:" + userId, String.valueOf(status), Duration.ofSeconds(ttlInSeconds));
	}


	public Long saveId(final GoogleUserRequest googleUserRequest){
		User user = userRepository.findBySocialDomainAndSocialIdentify(SocialDomain.GOOGLE, googleUserRequest.getId())
				.orElseGet(() -> {
					User newUser = User.toGoogleEntity(googleUserRequest);
					userRepository.save(newUser);
					return newUser;
				});

		return user.getUserId();
	}


	public void checkNickname(Long userId, String nickname) {
		User user = findUserById(userId);

		// 기존 닉네임과 동일한 경우 오류발생
		if(user.getNickname().equals(nickname)) throw new UnAuthorizedException(U004);

		// 이미 존재하는 닉네임이라면 오류발생
		if(userRepository.existsByNickname(nickname)) throw new UnAuthorizedException(U003);
	}

	public void registerNickname(Long userId, String nickname) {
		User user = findUserById(userId);
		if(userRepository.existsByNickname(nickname)) {
			throw new UnAuthorizedException(U003);
		} else {
			user.setNickname(nickname);
			userRepository.save(user);
		}
	}

	private User findUserById(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
	}
}
