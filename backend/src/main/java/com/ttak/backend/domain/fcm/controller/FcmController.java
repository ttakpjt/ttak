package com.ttak.backend.domain.fcm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttak.backend.domain.fcm.dto.request.FcmMessageReq;
import com.ttak.backend.domain.fcm.dto.request.FcmTokenReq;
import com.ttak.backend.domain.fcm.service.FcmService;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.global.auth.annotation.UserPk;
import com.ttak.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
@Tag(name = "FCM API", description = "FireBase Cloud Message 토큰 관련 API")
@CrossOrigin("*")
public class FcmController {

	private final FcmService fcmService;

	@Operation(summary = "Fcm Token 저장", description = "기기에 TTAK 설치시 FcmToken DB저장")
	@PostMapping("/save")
	public ResponseEntity<CommonResponse<?>> saveFcmToken (@RequestBody final FcmTokenReq fcmTokenReq){
		log.info("========== Fcm Token 저장 시작 ==========");
		fcmService.saveFcmToken(fcmTokenReq);
		log.info("========== Fcm Token 저장 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}


	@Operation(summary = "Fcm 메세지 전송 (메세지)", description = "아이템과 메세지 중 메세지 전송을 위한 fcm사용")
	@PostMapping("/send/message")
	public ResponseEntity<CommonResponse<?>> sendMessageFcmToken (@UserPk final Long userId, @RequestBody final FcmMessageReq fcmMessageReq){
		log.info("========== Fcm 메세지 전송 시작 ==========");
		fcmService.sendMessage(userId, fcmMessageReq.getData(), fcmMessageReq.getUserId());
		log.info("========== Fcm 메세지 전송 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}

	@Operation(summary = "Fcm 메세지 전송 (아이템)", description = "아이템과 메세지 중 아이템 전송을 위한 fcm사용")
	@PostMapping("/send/item")
	public ResponseEntity<CommonResponse<?>> sendItemFcmToken (@UserPk final Long userId, @RequestBody final FcmMessageReq fcmMessageReq){
		log.info("========== Fcm Item 전송 시작 ==========");
		fcmService.sendEffect(userId, fcmMessageReq.getData(), fcmMessageReq.getUserId());
		log.info("========== Fcm Item 전송 종료 ==========");
		return ResponseEntity.ok(CommonResponse.success());
	}


}
