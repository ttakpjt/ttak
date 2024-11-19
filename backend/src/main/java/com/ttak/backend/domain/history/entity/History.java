package com.ttak.backend.domain.history.entity;

import com.ttak.backend.domain.fcm.entity.enumType.Item;
import com.ttak.backend.global.common.TimeBaseEntity;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long historyId;

	@Column(name = "attack_id")
	private Long attackId;

	@Column(name = "receive_id")
	private Long receiveId;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private Item type;

	@Column(name = "message")
	private String message;

	public static History of(Long attackId, Long receiveId, Item type, String message) {
		return History.builder()
			.attackId(attackId)
			.receiveId(receiveId)
			.type(type)
			.message(message)
			.build();
	}
}
