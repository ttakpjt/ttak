package com.ttak.backend.domain.history.service;

import org.springframework.stereotype.Service;

import com.ttak.backend.domain.history.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;
}
