package com.ttak.backend.domain.history.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.history.service.HistoryService;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
@Tag(name = "History API", description = "History 관련 API")
@CrossOrigin("*")
public class HistoryController {

	private final HistoryService historyService;

	@Operation(summary = "예시", description = "예시에 대한 설명")
	@PostMapping("/temp")
	public ResponseEntity<CommonResponse<?>> tmpMethod (){
		log.info("========== 임시 메소드 시작 ==========");

		log.info("========== 임시 메소드 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}
}
