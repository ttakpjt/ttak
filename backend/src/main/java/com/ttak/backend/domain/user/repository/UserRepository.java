package com.ttak.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttak.backend.domain.user.repository.customRepository.CustomUserRepository;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;
import com.ttak.backend.domain.user.repository.customRepository.CustomUserRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
	Optional<User> findBySocialDomainAndSocialIdentify(SocialDomain socialDomain, String socialIdentify);

	Optional<User> findByUserId(Long userId);
	Optional<User> findByEmail(String email);

}
