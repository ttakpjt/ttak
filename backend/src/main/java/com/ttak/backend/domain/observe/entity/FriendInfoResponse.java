package com.ttak.backend.domain.observe.entity;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FriendInfoResponse {
	private String friendName;
	private String friendId;
	private String friendImg;

	public FriendInfoResponse(String friendName, String friendId, String friendImg) {
		this.friendName = friendName;
		this.friendId = friendId;
		this.friendImg = friendImg;
	}
}
