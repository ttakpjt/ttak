package com.ttak.backend.domain.observe.dto.reqeust;

import java.time.LocalTime;
import java.util.List;

import lombok.Getter;

@Getter
public class AppInfoReq {
	private List<String> appName;
	private LocalTime startTime;
	private LocalTime endTime;
}
