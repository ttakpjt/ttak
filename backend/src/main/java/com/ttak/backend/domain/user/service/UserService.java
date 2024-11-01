package com.ttak.backend.domain.user.service;

import com.ttak.backend.domain.user.dto.reqeust.GoogleUserRequest;

public interface UserService {

	Long getUserId(Long id);

	void moveInfo(GoogleUserRequest googleUserRequest);
}
