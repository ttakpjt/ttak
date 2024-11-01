package com.ttak.backend.domain.user.repository.customRepository;

import java.util.Optional;

import com.ttak.backend.domain.user.entity.ProviderInfo;
import com.ttak.backend.domain.user.entity.User;

public interface CustomUserRepository {
	Optional<User> findOauthLoginUser(ProviderInfo providerInfo, String socialIdentify);
}
