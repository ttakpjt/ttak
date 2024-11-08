package com.ttak.android.domain.model

data class UserModel(
    val id: String,        // 사용자 ID
    val email: String,     // 사용자 이메일
    val profileImage: String? = null, // 프로필 이미지 URL (선택 사항)
    val token: String // fcm 토큰
)