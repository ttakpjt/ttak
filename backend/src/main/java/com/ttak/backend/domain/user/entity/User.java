package com.ttak.backend.domain.user.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.ttak.backend.domain.fcm.entity.Fcm;
import com.ttak.backend.domain.user.dto.reqeust.GoogleUserRequest;
import com.ttak.backend.domain.user.entity.enumFolder.Role;
import com.ttak.backend.domain.user.entity.enumFolder.SocialDomain;
import com.ttak.backend.global.common.TimeBaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET delete_yn = true WHERE user_id = ?")
@SQLRestriction("delete_yn = false")
public class User extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email")
	private String email;

	@Column(name = "nickname")
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

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private Fcm fcm;


	//===============여기서부턴 Entity 기본 정보==================

	// @Builder.Default
	// @Column(name = "create_at", nullable = false, updatable = false)
	// private LocalDateTime createAt = LocalDateTime.now();
	//
	// @Builder.Default
	// @Column(name = "update_at", nullable = false)
	// private LocalDateTime updateAt = LocalDateTime.now();;
	//
	// @Column(name = "create_who")
	// private Long createWho;
	//
	// @Column(name = "update_who")
	// private Long updateWho;

	@Builder.Default
	@Column(name = "delete_yn", nullable = false)
	private boolean deleteYn = false;

	//===============여기서부턴 OAuth 소셜 인증 정보==================

	@Enumerated(EnumType.STRING)
	@Column(name = "social_domain", nullable = false)
	private SocialDomain socialDomain;

	@Column(name = "social_identify", nullable = false)
	private String socialIdentify;


	public static User toGoogleEntity(GoogleUserRequest googleUserRequest){
		return User.builder()
			.userId(System.currentTimeMillis())
			.email(googleUserRequest.getEmail())
			.role(Role.USER)
			.profilePic(googleUserRequest.getProfileImage())
			.socialDomain(SocialDomain.GOOGLE)
			.socialIdentify(googleUserRequest.getId())
			.build();
	}

}
