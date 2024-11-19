package com.ttak.backend.domain.user.dto.reqeust;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class GoogleUserRequest {

	private String id;
	private String email;
	private String profileImage;
	private String fcmToken;

}
