package com.ttak.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.user.dto.reqeust.BugContentsRequest;
import com.ttak.backend.domain.user.service.BugService;
import com.ttak.backend.global.auth.annotation.UserPk;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ban")
@Tag(name = "ban API", description = "사용금지 어플리케이션 설정 관련 api")
@CrossOrigin("*")
public class BanController {

	private final BugService bugService;

	@Operation(summary = "버그 제보 API", description = "사용중 예상치 못한 버그가 발생했을 경우 해당 시간과 사용자 정보를 기록하기 위한 API")
	@GetMapping("/test")
	public ResponseEntity<CommonResponse<?>> saveBug(@UserPk final Long userId, final BugContentsRequest bugContentsRequest) {
		log.info("========== 테스트 시작 ==========");
		bugService.saveBugReport(userId, bugContentsRequest.getContents());
		log.info("========== 테스트 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}
}
