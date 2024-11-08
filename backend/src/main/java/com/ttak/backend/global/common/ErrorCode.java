package com.ttak.backend.global.common;

public enum ErrorCode {
	// TEST 관련 예외 코드
	T000("테스트 오류 입니다."),

	// USER 관련 예외 코드
	U000("OAuth2 로그인 진행시 소셜로그인 기록이 없는 사용자입니다."),
	U001("해당 PK를 가진 유저가 존재하지 않습니다."),
	U002("회원가입, 로그인, 회원 조회 등 유저와 관련된 에러"),
	U003("중복된 닉네임 입니다."),
	U004("현재 등록된 닉네임과 동일한 닉네임 입니다."),
	U005("닉네임은 공백이 불가합니다."),

	// AUTH 관련 예외 코드
	A001("인증/인가시, 만료된 JWT"),
	A002("허가되지 않은 OAuth 로그인 도메인입니다."),
	A003("인증/인가 과정에서 AccessToken이 존재하지 않습니다."),
	A004("JWT 구조가 잘못 설정되어있거나 Base64Url 인코딩이 완료되지 않았습니다."),
	A005("비밀키가 잘못되었거나 JWT가 변조되어 검증할 수 없습니다."),
	A006("Token 검증과정에서 오류가 발생했습니다."),
	A007("AccessToken 내 유저정보가 실재하지 않는 정보입니다."),

	// BanList, BanApp 관련 예외 코드
	B000(""),


	// Friend 관련 예외 코드
	F000("이미 존재하는 친구입니다."),
	F001("Fcm 토큰이 등록되지 않은 비정상적 사용자입니다."),


	// FCM 관련 예외 코드
	FCM000("FcmToken이 등록되지 않는 사용자입니다."),
	FCM001("Fcm Message 발송 중 오류가 발생했습니다."),

	// Global 예외

	;
	private final String message;

	ErrorCode(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
