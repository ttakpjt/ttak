package com.ttak.backend.domain.history.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HistoryListRes {

	private Long count;
	private String Type;
	private String message;
	private LocalDateTime createAt;


}
