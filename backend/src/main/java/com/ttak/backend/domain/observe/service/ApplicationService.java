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

		BanList banList = banListRepository.findBanListByUser(user)
			.orElseGet(() -> {
				BanList newList = BanList.toEntity(user, appInfoReq);
				banListRepository.save(newList);
				return newList;
			});

		List<BanApp> list = new ArrayList<>();
		for(String app : appInfoReq.getAppName()){
			list.add(BanApp.toEntity(banList, app));
		}

		banAppRepository.saveAll(list);
	}



	private User findUserById(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.U001));
	}
}
