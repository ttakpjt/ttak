package com.ttak.android.domain.model

data class UserModel(
    val id: String,        // 사용자 ID
    val email: String,     // 사용자 이메일
    val profileImage: String, // 프로필 이미지 URL
    val fcmToken: String    // fcm 토큰
)