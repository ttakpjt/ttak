package com.ttak.backend.domain.observe.service;

import org.springframework.stereotype.Service;

import com.ttak.backend.domain.observe.dto.reqeust.AppInfoReq;
import com.ttak.backend.domain.observe.repository.BanAppRepository;
import com.ttak.backend.domain.observe.repository.BanListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final BanAppRepository banAppRepository;
	private final BanListRepository banListRepository;

	public void register(Long userId, AppInfoReq appInfoReq){

	}

}
