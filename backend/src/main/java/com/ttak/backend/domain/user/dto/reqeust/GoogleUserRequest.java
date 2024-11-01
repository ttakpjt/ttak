package com.ttak.backend.domain.user.dto.reqeust;

import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;

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
	private String name;
	private String profileImage;

	@Builder.Default
	private SocialDomain socialDomain = SocialDomain.GOOGLE;

}
