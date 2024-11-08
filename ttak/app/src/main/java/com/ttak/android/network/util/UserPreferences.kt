package com.ttak.android.network.util

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // 백에서 받아온 유저 아이디 저장
    fun saveUserId(userId: String) {
        preferences.edit().putString("user_id", userId).apply()
    }

    // 백에서 받아온 유저 아이디 가져오기
    fun getUserId(): String? {
        return preferences.getString("user_id", null)
    }

    // 백에서 받아온 유저 아이디 삭제
    fun clearUserId() {
        preferences.edit().remove("user_id").apply()
    }

    // 사용자 닉네임 설정
    fun saveNickname(nickname: String) {
        preferences.edit().putString("nickname", nickname).apply()
    }

    // 사용자 닉네임 가져오기
    fun getNickname(): String? {
        return preferences.getString("nickname", null)
    }

    // 저장한 사용자 닉네임 삭제
    fun clearNickname() {
        preferences.edit().remove("nickname").apply()
    }
}