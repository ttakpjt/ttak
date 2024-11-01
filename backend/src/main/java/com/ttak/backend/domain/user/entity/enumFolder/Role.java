package com.ttak.backend.domain.user.entity.enumFolder;

import lombok.Getter;

@Getter
public enum Role {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN"),
	NOT_REGISTERED("ROLE_NOT_REGISTERED");

	private final String key;

	Role(String key) {
		this.key = key;
	}

}
