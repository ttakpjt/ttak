package com.ttak.backend.domain.fcm.service;

import static com.google.firebase.messaging.AndroidConfig.Priority.*;
import static com.ttak.backend.global.common.ErrorCode.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ttak.backend.domain.fcm.dto.request.FcmTokenReq;
import com.ttak.backend.domain.fcm.entity.Fcm;
import com.ttak.backend.domain.fcm.entity.enumType.Item;
import com.ttak.backend.domain.fcm.repository.FcmRepository;
import com.ttak.backend.domain.history.service.HistoryService;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.exception.FirebaseMessagingException;
import com.ttak.backend.global.exception.NotFoundException;
import com.ttak.backend.global.util.IdempotencyUtil;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FcmService{

	private static final Logger log = LoggerFactory.getLogger(FcmService.class);
	private final FirebaseMessaging firebaseMessaging;
	private final FcmRepository fcmRepository;
	private final UserRepository userRepository;
	private final IdempotencyUtil idempotencyUtil;
	private final HistoryService historyService;

	/**
	 * Fcm을 통해 받아온 해당 유저의 fcmToken을 DB에 저장한다.
	 * @param fcmTokenReq
	 */
	public void saveFcmToken(final FcmTokenReq fcmTokenReq) {
		// 해당 유저가 가지고 있는 Fcm객체 불러오기 (없다면 객체 생성)
		Fcm fcm = fcmRepository.findByDeviceSerialNumber(fcmTokenReq.getDeviceSerialNumber())
			.orElseGet(() -> Fcm.toEntity(fcmTokenReq));

		// fcmToken이 비어있거나 바뀌었다면 fcm객체의 token을 변경한다.
		if(fcm.getFcmToken() == null || !fcm.getFcmToken().equals(fcmTokenReq.getToken())){
			fcm.changeFcmToken(fcmTokenReq.getToken());
		}
		fcmRepository.save(fcm);
	}

	/**
	 * [메세지] 해당 인원에게 메세지 전송
	 * @param sendId
	 * @param data
	 * @param receiveId
	 */
	public void sendMessage(final Long sendId, final String data, final Long receiveId) {
		if(!userRepository.existsById(sendId) || !userRepository.existsById(receiveId)) {
			throw new NotFoundException(U001);
		}
		String fcmKey = "fcmMessage_" + sendId + "_" + receiveId;
		String historyKey = "history_" + sendId + "_" + receiveId + "_" + data;

		Message message = Message.builder()
				.setToken(getFcmToken(receiveId))
				.setAndroidConfig(AndroidConfig.builder()
						.setPriority(AndroidConfig.Priority.HIGH)
						.setDirectBootOk(true)
						.build())
				.putData("title", "새로운 알림")
				.putData("body", data)
				.putData("channel_id", "incoming_call_channel")
				.putData("priority", "high")
				.build();

		try {
			// redis에 최근 공격기록이 존재한다면 fcm메세지를 날리지 않고, 존재하지 않는다면 redis에 기록 후 fcm메세지 전송
			if(idempotencyUtil.addFcmRequest(fcmKey)) firebaseMessaging.send(message);

			// redis에 최근 공격기록이 존재한다면 history를 저장하지 않고, 존재하지 않는다면 redis에 기록 후 history 저장
			if(idempotencyUtil.addHistoryRequest(historyKey)) {
				historyService.addAttackHistory(sendId, data, receiveId, Item.USER_MESSAGE);
			}
		} catch (FirebaseMessagingException | com.google.firebase.messaging.FirebaseMessagingException e) {
			throw new FirebaseMessagingException(FCM001);
		}
	}

	/**
	 * [아이템] 해당 인원에게 선택된 아이템으로 구성된 이펙트 전송
	 * @param sendId
	 * @param data
	 * @param receiveId
	 */
	public void sendEffect(final Long sendId, final String data, final Long receiveId) {
		if(!userRepository.existsById(sendId) || !userRepository.existsById(receiveId)) {
			throw new NotFoundException(U001);
		}

		String fcmKey = "fcmMessage_" + sendId + "_" + receiveId;
		String historyKey = "history_" + sendId + "_" + receiveId + "_" + data;

		Message message = Message.builder()
				.setToken(getFcmToken(receiveId))
				.setAndroidConfig(AndroidConfig.builder()
						.setPriority(AndroidConfig.Priority.HIGH)
						.setDirectBootOk(true)
						.build())
				.putData("title", "새로운 알림")
				.putData("body", data)
				.putData("channel_id", "incoming_call_channel")
				.putData("priority", "high")
				.putData("animation", data)
				.build();

		try {
			// redis에 최근 공격기록이 존재한다면 fcm메세지를 날리지 않고, 존재하지 않는다면 redis에 기록 후 fcm메세지 전송
			if(idempotencyUtil.addFcmRequest(fcmKey)) firebaseMessaging.send(message);

			// redis에 최근 공격기록이 존재한다면 history를 저장하지 않고, 존재하지 않는다면 redis에 기록 후 history 저장
			if(idempotencyUtil.addHistoryRequest(historyKey)) {
				historyService.addAttackHistory(sendId, data + "공격을 받았습니다.", receiveId, Item.valueOf(data));
			}
		} catch (FirebaseMessagingException | com.google.firebase.messaging.FirebaseMessagingException e) {
			throw new FirebaseMessagingException(FCM001);
		}
	}

	/**
	 * 입력 들어온 UserId를 이용하여 fcmToken을 반환한다.
	 * @param userId
	 * @return
	 */
	private String getFcmToken(final Long userId) {
		// 유저 객체 불러오기 (없다면 오류 발생 404)
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(U001));

		// 해당 유저가 가지고 있는 Fcm객체 불러오기 (없으면 오류 발생 404)
		Fcm fcm = fcmRepository.findByUser(user)
			.orElseThrow(() -> new NotFoundException(FCM000));

		return fcm.getFcmToken();
	}

}
