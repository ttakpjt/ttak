package com.ttak.backend.domain.observe.entity;
import lombok.Getter;


@Getter
public class StatusUpdateMessage {
	private Long userId;      // 금지된 행동을 한 사용자 ID
	private int status;   // 금지된 행동의 유형 0:Green, 1:red

	private StatusUpdateMessage(Long userId, int status) {
		this.userId = userId;
		this.status = status;
	}

	public static StatusUpdateMessage of(Long userId, int status) {
		return new StatusUpdateMessage(userId, status);
	}

}

