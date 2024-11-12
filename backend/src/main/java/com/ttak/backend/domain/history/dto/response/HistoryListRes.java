package com.ttak.backend.domain.history.dto.response;

import java.time.LocalDateTime;

import com.ttak.backend.domain.fcm.entity.enumType.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HistoryListRes {

	private Item Type;
	private String message;
	private LocalDateTime createAt;


}
