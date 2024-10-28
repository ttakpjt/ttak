package com.ttak.backend.global.exception;

import com.ttak.backend.global.common.ErrorCode;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
	private ErrorCode errorCode;

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
