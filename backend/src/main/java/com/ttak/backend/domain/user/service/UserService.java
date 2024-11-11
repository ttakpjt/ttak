package com.ttak.backend.domain.user.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ttak.backend.domain.fcm.entity.Fcm;
import com.ttak.backend.domain.fcm.repository.FcmRepository;
import com.ttak.backend.domain.observe.repository.FriendRepository;
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
	private final FriendRepository friendRepository;
	private final FcmRepository fcmRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String USER_STATUS_KEY_PREFIX = "user:status:";

	public Long getUserId(Long id) {
		// if(id >= 10) throw new NotFoundException(T000);
		return id;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<UserInfoResponse> searchUsersByNickname(String nickname, Long currentUserId) {
		System.out.println(nickname);
		return userRepository.findUsersWithRelation(currentUserId, nickname);
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

	/**
	 * 로그인당시 해당 회원정보를 DB에 이관한다. 이 때, FcmToken도 저장하게 된다.
	 * @param googleUserRequest
	 * @return
	 */
	public Long saveId(final GoogleUserRequest googleUserRequest){
		// User 정보를 불러온다. 만약 정보가 없다면 회원가입을 진행한다.
		User user = userRepository.findBySocialDomainAndEmail(SocialDomain.GOOGLE, googleUserRequest.getEmail())
				.orElseGet(() -> {
					User newUser = User.toGoogleEntity(googleUserRequest);
					userRepository.save(newUser);
					return newUser;
				});

		// 파라미터로 들어온 fcm정보를 DB에서 찾아온다. 만약 정보가 없다면 오류반환 (F001)
		Fcm fcm = fcmRepository.findByFcmToken(googleUserRequest.getFcmToken())
			.orElseThrow(() -> new NotFoundException(F001));

		// 불러온 fcm 정보에 해당 유저를 저장한다.
		fcm.setUser(user);
		fcmRepository.save(fcm);

		// Redis 유저 상태 등록
		setUserStatus(user.getUserId(), 0, 86400); // 24시간 TTL 설정

		// 값 반환
		return user.getUserId();
	}

	/**
	 * 해당 유저와 매핑된 FCM 테이블 데이터를 삭제한다.
	 * 이후 해당 기기로 타 유저가 로그인했을 때 재매핑.
	 * @param userId 로그아웃 요청한 사용자를 fcm 테이블에서 제외한다.
	 */
	public void logout(Long userId) {
		// 해당 UserId를 가지는 User 객체를 찾는다.
		User user = findUserById(userId);

		// 해당 User 객체와 매핑되어있는 Fcm 객체를 찾는다. (없을경우 예외반환 F001)
		Fcm fcm = fcmRepository.findByUser(user)
			.orElseThrow(() -> new NotFoundException(F001));

		// 해당 Fcm 객체의 User 값을 Null로 변경한다.
		fcm.setUser(null);
		fcmRepository.save(fcm);
	}


	public void checkNickname(String nickname) {
		// 비어있는 닉네임은 불가
		if(nickname.isBlank() || nickname.isEmpty()) throw new UnAuthorizedException(U005);

		// 이미 존재하는 닉네임이라면 오류발생
		if(userRepository.existsByNickname(nickname)) throw new UnAuthorizedException(U003);
	}

	public void registerNickname(Long userId, String nickname) {
		User user = findUserById(userId);

		// 기존 닉네임과 동일한 경우 오류발생
		if(user.getNickname() != null){
			if(user.getNickname().equals(nickname)) throw new UnAuthorizedException(U004);
		}

		// 이미 존재하는 닉네임이라면 오류발생, 아니라면 닉네임 변경
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
