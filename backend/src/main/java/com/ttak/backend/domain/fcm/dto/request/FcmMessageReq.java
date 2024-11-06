package com.ttak.backend.domain.fcm.dto.request;

import lombok.Getter;

@Getter
public class FcmMessageReq {

	private Long userId;
	private String data;
}
