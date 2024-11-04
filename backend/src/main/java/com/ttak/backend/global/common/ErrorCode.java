package com.ttak.backend.global.common;

public enum ErrorCode {
	// TEST 관련 예외 코드
	T000("테스트 오류 입니다."),

	// USER 관련 예외 코드
	U000("회원가입, 로그인, 회원 조회 등 유저와 관련된 에러"),
	U001("해당 아이디를 가진 유저가 존재하지 않습니다."),

	// AUTH 관련 예외 코드
	A001("인증/인가시, 만료된 JWT"),

	// Friend 관련 예외 코드
	F000("이미 존재하는 친구입니다.");

	// Global 예외


	private final String message;

	ErrorCode(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
