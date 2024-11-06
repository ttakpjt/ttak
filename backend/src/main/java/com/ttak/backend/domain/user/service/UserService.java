package com.ttak.backend.domain.user.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import org.springframework.stereotype.Service;

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

}
