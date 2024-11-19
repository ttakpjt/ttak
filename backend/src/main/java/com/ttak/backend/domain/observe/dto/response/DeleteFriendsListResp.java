package com.ttak.backend.domain.observe.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class DeleteFriendsListResp {

    private Long userId;
    private String nickname;
    private String profilePic;
    private LocalDateTime updateAt;



}
