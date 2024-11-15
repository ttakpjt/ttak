package com.ttak.backend.domain.observe.dto.reqeust;


import com.ttak.backend.domain.observe.entity.Friend;
import com.ttak.backend.domain.user.entity.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateFriendReq {
	private Long userId;
	private Long followingId;

	private CreateFriendReq(){}

	private CreateFriendReq(Long userId, Long followingId){
		this.userId = userId;
		this.followingId = followingId;
	}

	public static CreateFriendReq of(Long userId, Long followingId){
		return new CreateFriendReq(userId, followingId);
	}

	// DTO -> Entity
	public static Friend toFriend(User userId, User followingId){
		return Friend.builder().userId(userId).followingId(followingId).build();
	}
}
