package com.ttak.backend.domain.fcm.dto.request;

import lombok.Getter;

@Getter
public class FcmTokenReq {
	private Long userId;
	private String token;
}
