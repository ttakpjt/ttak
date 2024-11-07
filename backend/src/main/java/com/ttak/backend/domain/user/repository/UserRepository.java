package com.ttak.backend.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;
import com.ttak.backend.domain.user.repository.customRepository.CustomUserRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
	Optional<User> findBySocialDomainAndSocialIdentify(SocialDomain socialDomain, String socialIdentify);

	Optional<User> findByUserId(Long userId);

	boolean existsByNickname(String nickName);

	// 닉네임을 포함하는 사용자 검색
	List<User> findByNicknameContainingIgnoreCase(String nickname);

	//사용자 아이디만 조회
	@Query("SELECT u.userId FROM User u")
	List<Long> findAllUserIds();
}
