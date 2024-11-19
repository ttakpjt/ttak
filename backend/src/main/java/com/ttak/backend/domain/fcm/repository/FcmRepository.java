package com.ttak.backend.domain.fcm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttak.backend.domain.fcm.entity.Fcm;
import com.ttak.backend.domain.user.entity.User;

public interface FcmRepository extends JpaRepository<Fcm, Long> {

	Optional<Fcm> findByDeviceSerialNumber(String deviceSerialNumber);

	Optional<Fcm> findByUser(User user);

	Optional<Fcm> findByFcmToken(String fcmToken);

	boolean existsByUser(User user);

}
