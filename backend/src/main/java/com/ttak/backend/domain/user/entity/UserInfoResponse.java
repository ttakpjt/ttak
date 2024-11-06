package com.ttak.backend.domain.user.entity;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserInfoResponse {
	private String UserName;
	private Long UserId;
	private String UserImg;

	public UserInfoResponse(String UserName, Long UserId, String UserImg) {
		this.UserName = UserName;
		this.UserId = UserId;
		this.UserImg = UserImg;
	}
}
