package com.ttak.backend.global.exception;

import com.ttak.backend.global.common.ErrorCode;

import lombok.Getter;

@Getter
public class UnAuthorizedException extends RuntimeException {
	private final ErrorCode errorCode;

	public UnAuthorizedException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
