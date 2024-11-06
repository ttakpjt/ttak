package com.ttak.backend.domain.user.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.UserInfoResponse;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

	private final UserRepository userRepository;

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

}
