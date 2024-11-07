package com.ttak.backend.domain.observe.dto;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FriendInfoResponse {
	private String friendName;
	private Long friendId;
	private String friendImg;
	private int status;


	@QueryProjection
	public FriendInfoResponse(String friendName, Long friendId, String friendImg) {
		this.friendName = friendName;
		this.friendId = friendId;
		this.friendImg = friendImg;
	}

	public void updateStatus(Integer status) {
		this.status = status;
	}
}
