package com.ttak.backend.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomPkUtil {

	public Long makeRandomPk () {
		String dateTimePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String millis = String.format("%03d", System.currentTimeMillis() % 1000);  // 밀리초 부분만 추출(3자리)
		String uuidPart =  String.format("%02d", ThreadLocalRandom.current().nextInt(0, 99)); // 2자리 랜덤 숫자

		return Long.parseLong(dateTimePart + millis + uuidPart);
	}
}
