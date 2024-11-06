package com.ttak.backend.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.entity.UserInfoResponse;
import com.ttak.backend.domain.user.service.UserService;
import com.ttak.backend.global.auth.annotation.authUser;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User API", description = "회원가입 및 정보확인을 위한 API")
@CrossOrigin("*")
public class UserController {

	private final UserService userService;

	@Operation(summary = "[완료] 테스트 api", description = "테스트를 위한 임시 api 생성")
	@GetMapping("/test/{userId}")
	public ResponseEntity<CommonResponse<?>> test(@PathVariable final Long userId) {
		log.info("========== 테스트 시작 ==========");
		Long userIds = userService.getUserId(userId);
		log.info("========== 테스트 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success(userIds));
	}

	@Operation(summary = "[완료] Annotation 테스트 api", description = "Custom Annotation 확인")
	@GetMapping("/test/annotation")
	public ResponseEntity<CommonResponse<?>> annotationTest(@authUser User user) {
		log.info("========== 테스트 2 시작 ==========");
		if (user != null) {
			log.info("로그인한 userID: " + user.getUserId());
		} else {
			log.warn("User 객체가 null입니다.");
		}
		log.info("========== 테스트 2 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}

	@GetMapping("/search")
	public ResponseEntity<?> userSearch(@RequestParam("nickname") String nickname) {
		List<UserInfoResponse> users = userService.searchUsersByNickname(nickname);
		return ResponseEntity.ok(users);
	}

}
