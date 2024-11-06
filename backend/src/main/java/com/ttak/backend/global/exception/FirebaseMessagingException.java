package com.ttak.backend.global.exception;

import com.ttak.backend.global.common.ErrorCode;

import lombok.Getter;

@Getter
public class FirebaseMessagingException extends RuntimeException {
	private ErrorCode errorCode;

	public FirebaseMessagingException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
