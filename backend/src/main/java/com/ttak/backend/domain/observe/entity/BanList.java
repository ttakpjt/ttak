package com.ttak.backend.domain.observe.entity;

import java.time.LocalDateTime;

import com.ttak.backend.domain.observe.dto.reqeust.AppInfoReq;
import com.ttak.backend.domain.user.entity.User;
import com.ttak.backend.global.common.TimeBaseEntity;
import com.ttak.backend.global.util.RandomPkUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BanList extends TimeBaseEntity {

	@Id
	@Column(name="ban_list_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	private LocalDateTime startTime;

	private LocalDateTime endTime;


	public static BanList toEntity (User user, AppInfoReq appInfoReq) {
		return BanList.builder()
			.id(new RandomPkUtil().makeRandomPk())
			.user(user)
			.startTime(appInfoReq.getStartTime())
			.endTime(appInfoReq.getEndTime())
			.build();
	}

	public void setTime(AppInfoReq appInfoReq) {
		this.startTime = appInfoReq.getStartTime();
		this.endTime = appInfoReq.getEndTime();
	}
}
