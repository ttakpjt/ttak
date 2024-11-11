package com.ttak.backend.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.user.dto.reqeust.GoogleUserRequest;
import com.ttak.backend.domain.user.dto.reqeust.NicknameReq;
import com.ttak.backend.domain.user.dto.response.UserInfoResponse;
import com.ttak.backend.domain.user.dto.response.UserRegistRes;
import com.ttak.backend.domain.user.service.UserService;
import com.ttak.backend.global.auth.annotation.UserPk;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User API", description = "회원가입 및 정보확인을 위한 API")
@CrossOrigin("*")
public class UserController {

	private final UserService userService;


	@Operation(summary = "[완료] 테스트 api", description = "테스트를 위한 임시 api 생성")
	@GetMapping("/test")
	public ResponseEntity<CommonResponse<?>> test(@UserPk final Long userId) {
		log.info("========== 테스트 시작 ==========");
		Long userIds = userService.getUserId(userId);
		log.info("========== 테스트 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success(userIds));
	}

	@GetMapping("/search")
	public ResponseEntity<CommonResponse<?>> userSearch(@RequestParam("nickname") String nickname, @UserPk Long userId) {
		List<UserInfoResponse> users = userService.searchUsersByNickname(nickname, userId);
		return ResponseEntity.ok(CommonResponse.success(users));
	}

	@Operation(summary = "로그인한 유저 정보 DB이관", description = "google로그인 유저 정보 받아오면 UserId 반환")
	@PostMapping("/save")
	public ResponseEntity<CommonResponse<?>> saveUser(@RequestBody final GoogleUserRequest googleUserRequest) {
		log.info("========== 유저정보 DB 이관 시작 ==========");
		UserRegistRes result = UserRegistRes.toEntity(userService.saveId(googleUserRequest));
		log.info("========== 유저정보 DB 이관 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success(result));
	}

	@Operation(summary = "로그아웃 진행", description = "현재 로그인한 사용자 로그아웃 진행")
	@GetMapping("/logout")
	public ResponseEntity<CommonResponse<?>> logout(@UserPk final Long userId) {
		log.info("========== 로그아웃 시작 ==========");
		userService.logout(userId);
		log.info("========== 로그아웃 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}

	@Operation(summary = "닉네임 중복확인", description = "들어온 닉네임과 동일한 닉네임이 존재하는지 확인, 중복 닉네임이 없다면 상태 200 반환")
	@PostMapping("/check/nickname")
	public ResponseEntity<CommonResponse<?>> checkNickname(@RequestBody final NicknameReq nicknameReq) {
		log.info("========== 닉네임 중복확인 시작 ==========");
		userService.checkNickname(nicknameReq.getNickname());
		log.info("========== 닉네임 중복확인 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}

	@Operation(summary = "닉네임 등록/수정", description = "파라미터로 들어온 닉네임으로 수정 및 등록 진행")
	@PostMapping("/register/nickname")
	public ResponseEntity<CommonResponse<?>> registerNickname(@UserPk final Long userId, @RequestBody final NicknameReq nicknameReq) {
		log.info("========== 닉네임 등록 시작 ==========");
		userService.registerNickname(userId, nicknameReq.getNickname());
		log.info("========== 닉네임 등록 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}

	@Operation(summary = "닉네임 유무", description = "해당 유저의 닉네임이 존재하는지 아닌지 반환")
	@GetMapping("/exist/nickname")
	public ResponseEntity<CommonResponse<?>> existNickname(@UserPk final Long userId) {
		log.info("========== 닉네임 등록여부 확인 시작 ==========");
		boolean check = userService.existNickname(userId);
		log.info("========== 닉네임 등록여부 확인 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success(check));
	}


}
