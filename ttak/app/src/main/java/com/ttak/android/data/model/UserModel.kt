package com.ttak.android.data.model

data class UserModel(
    val id: String,        // 사용자 ID
    val name: String,      // 사용자 이름
    val email: String,     // 사용자 이메일
    val profileImage: String? = null // 프로필 이미지 URL (선택 사항)
)