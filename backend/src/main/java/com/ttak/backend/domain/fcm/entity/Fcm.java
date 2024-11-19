package com.ttak.backend.domain.fcm.entity;

import com.ttak.backend.domain.fcm.dto.request.FcmTokenReq;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.global.common.TimeBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Fcm extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fcm_id")
	private Long fcmId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String deviceSerialNumber;

	private String fcmToken;

	public static Fcm toEntity(FcmTokenReq fcmTokenReq) {
		return Fcm.builder()
			.deviceSerialNumber(fcmTokenReq.getDeviceSerialNumber())
			.fcmToken(fcmTokenReq.getToken())
			.build();
	}

	public void changeFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
