package com.ttak.backend.domain.fcm.service;

import static com.ttak.backend.global.common.ErrorCode.*;

import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ttak.backend.domain.fcm.entity.Fcm;
import com.ttak.backend.domain.fcm.repository.FcmRepository;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.exception.FirebaseMessagingException;
import com.ttak.backend.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FcmService{

	private final FirebaseMessaging firebaseMessaging;
	private final FcmRepository fcmRepository;
	private final UserRepository userRepository;

	/**
	 * Fcm을 통해 받아온 해당 유저의 fcmToken을 DB에 저장한다.
	 * @param userId
	 * @param fcmToken
	 */
	public void saveFcmToken(final Long userId, final String fcmToken) {
		// 유저 객체 불러오기 (없다면 오류 발생 404)
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(U001));

		// 해당 유저가 가지고 있는 Fcm객체 불러오기 (없다면 객체 생성)
		Fcm fcm = fcmRepository.findByUser(user)
			.orElseGet(() -> Fcm.of(user, fcmToken));

		// fcmToken이 비어있거나 바뀌었다면 fcm객체의 token을 변경한다.
		if(fcm.getFcmToken() == null || !fcm.getFcmToken().equals(fcmToken)){
			fcm.changeFcmToken(fcmToken);
		}

		fcmRepository.save(fcm);
	}

	/**
	 * [메세지] 해당 인원에게 메세지 전송
	 * @param data
	 * @param userId
	 */
	public void sendMessage(final String data, final Long userId) {
		// message 객체 생성 (firebase)
		Message message = Message.builder()
			.setToken(getFcmToken(userId))
			.setNotification(Notification.builder()
				.setBody(data)
				.build())
			.build();

		try {
			firebaseMessaging.send(message);
		} catch (FirebaseMessagingException | com.google.firebase.messaging.FirebaseMessagingException e) {
			throw new FirebaseMessagingException(FCM001);
		}
	}

	/**
	 * [아이템] 해당 인원에게 선택된 아이템으로 구성된 이펙트 전송
	 * @param data
	 * @param userId
	 */
	public void sendEffect(final String data, final Long userId)  {
		// message 객체 생성 (firebase)
		Message message = Message.builder()
			.setToken(getFcmToken(userId))
			.putData("animation", data)
			.build();

		try {
			firebaseMessaging.send(message);
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
