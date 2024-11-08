package com.ttak.backend.domain.history.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryCount {
	private Long totalCount;   // 전체 사용자의 누적 횟수
	private Long myCount;      // 나의 누적 횟수
	private Long friendsCount; // 내 친구들의 누적 횟수

	public static HistoryCount of(Long totalCount, Long myCount, Long friendsCount) {
		return HistoryCount.builder()
			.totalCount(totalCount)
			.myCount(myCount)
			.friendsCount(friendsCount).build();
	}

}
