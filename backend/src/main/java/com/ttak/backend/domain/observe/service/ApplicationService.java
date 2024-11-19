package com.ttak.backend.domain.observe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttak.backend.domain.observe.dto.reqeust.AppInfoReq;
import com.ttak.backend.domain.observe.entity.BanApp;
import com.ttak.backend.domain.observe.entity.BanList;
import com.ttak.backend.domain.observe.repository.BanAppRepository;
import com.ttak.backend.domain.observe.repository.BanListRepository;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.domain.user.repository.UserRepository;
import com.ttak.backend.global.common.ErrorCode;
import com.ttak.backend.global.exception.NotFoundException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final BanAppRepository banAppRepository;
	private final BanListRepository banListRepository;
	private final UserRepository userRepository;

	@Transactional
	public void register(Long userId, AppInfoReq appInfoReq){
		User user = findUserById(userId);

		// 요청한 유저의 사용금지 시간대 탐색
		BanList banList = banListRepository.findBanListByUser(user)
			.orElseGet(() -> {
				BanList newList = BanList.toEntity(user, appInfoReq);
				banListRepository.save(newList);
				return newList;
			});

		// 해당 시간대에 할당된 사용금지 어플리케이션 리스트 삭제
		banAppRepository.deleteAllByBanList(banList);

		// 시작시간과 끝나는 시간이 다를경우 업데이트
		if(!banList.getStartTime().equals(appInfoReq.getStartTime()) || !banList.getEndTime().equals(appInfoReq.getEndTime())){
			banList.setTime(appInfoReq);
		}

		// 해당 banList 에 설정들어온 어플리케이션 등록
		List<BanApp> list = appInfoReq.getAppName().stream()
			.map(app -> BanApp.toEntity(banList, app))
			.toList();

		banAppRepository.saveAll(list);
	}



	private User findUserById(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
	}
}
