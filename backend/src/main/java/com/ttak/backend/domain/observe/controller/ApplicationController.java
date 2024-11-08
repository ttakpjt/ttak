package com.ttak.backend.domain.observe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.observe.dto.reqeust.AppInfoReq;
import com.ttak.backend.domain.observe.service.ApplicationService;
import com.ttak.backend.global.auth.annotation.UserPk;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/application")
@Tag(name = "application API", description = "사용금지 어플리케이션 설정 관련 api")
@CrossOrigin("*")
public class ApplicationController {

	private final ApplicationService applicationService;

	@PostMapping("/setting")
	@Operation(summary = "사용금지 어플리케이션 등록")
	public ResponseEntity<CommonResponse<?>> addApplication (@UserPk Long userId, @RequestBody final AppInfoReq appInfoReq){
		log.info("========== 사용금지 항목 등록 시작 ==========");
		applicationService.register(userId, appInfoReq);
		log.info("========== 사용금지 항목 등록 시작 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}



}
