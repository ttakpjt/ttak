package com.ttak.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.user.service.UserService;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "User API", description = "회원가입 및 정보확인을 위한 API")
@CrossOrigin("*")
public class UserController {

	private final UserService userService;

	@Operation(summary = "[완료] 테스트 api", description = "테스트를 위한 임시 api 생성")
	@GetMapping("/test/{userId}")
	public ResponseEntity<CommonResponse<?>> test(@PathVariable final Long userId) {
		log.info("========== 거래내역 조회 시작 ==========");
		Long userIds = userService.getUserId(userId);
		log.info("========== 거래내역 조회 시작 ==========");
		return ResponseEntity.ok(CommonResponse.success(userIds));
	}
}
