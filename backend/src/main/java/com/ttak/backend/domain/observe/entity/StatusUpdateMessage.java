package com.ttak.backend.domain.observe.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateMessage {
	private Long userId;      // 금지된 행동을 한 사용자 ID
	private UserStatus status;   // 금지된 행동의 유형


}

