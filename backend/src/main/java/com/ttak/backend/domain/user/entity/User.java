package com.ttak.backend.domain.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.ttak.backend.domain.user.entity.enumFolder.Role;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET delete_yn = true WHERE user_id = ?")
@SQLRestriction("delete_yn = false")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name="profile_pic")
	private String profilePic;

	@ColumnDefault("0")
	@Column(name="point", nullable = false)
	private int point;

	/**
	 *  1: 허용
	 * -1: 비혀용
	 */
	@ColumnDefault("1")
	@Column(name="search_permit", nullable = false)
	private int searchPermit;

	@ColumnDefault("1")
	@Column(name="expose_permit", nullable = false)
	private int exposePermit;

	@Enumerated(EnumType.STRING)
	@Column(name= "role", nullable = false)
	private Role role;


	//===============여기서부턴 Entity 기본 정보==================

	@Builder.Default
	@Column(name = "create_at", nullable = false, updatable = false)
	private LocalDateTime createAt = LocalDateTime.now();

	@Builder.Default
	@Column(name = "update_at", nullable = false)
	private LocalDateTime updateAt = LocalDateTime.now();;

	@Column(name = "create_who")
	private Long createWho;

	@Column(name = "update_who")
	private Long updateWho;

	@Builder.Default
	@Column(name = "delete_yn", nullable = false)
	private boolean deleteYn = false;

	//===============여기서부턴 OAuth 소셜 인증 정보==================

	@Enumerated(EnumType.STRING)
	@Column(name = "social_domain", nullable = false)
	private SocialDomain socialDomain;

	@Column(name = "social_identify", nullable = false)
	private String socialIdentify;

}
