package com.ttak.backend.domain.user.service;

import org.springframework.stereotype.Service;

import com.ttak.backend.domain.user.entity.Bug;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.BugRepository;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BugService {

	private final BugRepository bugRepository;
	private final UserRepository userRepository;

	public void saveBugReport(Long userId, String contents) {
		bugRepository.save(Bug.toEntity(findUserById(userId), contents));
	}

	private User findUserById(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
	}

}
