package com.ttak.backend.domain.observe.dto;


import com.ttak.backend.domain.observe.entity.Friend;
import com.ttak.backend.domain.user.entity.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateFriendRequest {
	private Long userId;
	private Long followingId;

	private  CreateFriendRequest(){}

	private CreateFriendRequest(Long userId, Long followingId){
		this.userId = userId;
		this.followingId = followingId;
	}

	public static CreateFriendRequest of(Long userId, Long followingId){
		return new CreateFriendRequest(userId, followingId);
	}

	// DTO -> Entity
	public static Friend toFriend(User userId, User followingId){
		return Friend.builder().userId(userId).followingId(followingId).build();
	}
}
