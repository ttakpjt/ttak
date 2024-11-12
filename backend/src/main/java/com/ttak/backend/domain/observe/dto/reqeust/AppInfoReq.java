package com.ttak.backend.domain.observe.dto.reqeust;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class AppInfoReq {
	private List<String> appName;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
