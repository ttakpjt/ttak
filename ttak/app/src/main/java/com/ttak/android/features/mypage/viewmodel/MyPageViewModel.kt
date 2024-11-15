package com.ttak.android.features.mypage.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ttak.android.data.repository.MyPageRepository
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ttak.android.network.implementation.MyPageApiImpl
import com.ttak.android.network.util.ApiConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody

class NicknameViewModel(application: Application) : AndroidViewModel(application) {
    // MyPageApi를 활용한 새로운 repository
    private val myPageRepository: MyPageRepository = MyPageApiImpl(
        ApiConfig.createMyPageApi(application)
    )

    // errorBody에서 message 추출하는 함수
    private fun getMessageFromErrorBody(errorBody: ResponseBody): String {
        // errorBody를 string으로 변환
        val errorString = errorBody.string()

        // Gson을 사용하여 JSON 파싱
        val jsonObject = Gson().fromJson(errorString, JsonObject::class.java)

        // "message" 키에 해당하는 값을 추출
        return jsonObject.get("message")?.asString ?: "서버 오류"  // 메시지가 없으면 "서버 오류" 반환
    }

    // checkNickname 메소드: 닉네임 중복 검사
    fun checkNickname(nickname: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val response = myPageRepository.checkNickname(nickname)

            if (response.isSuccessful) {
                // 닉네임 중복되지 않음 (200 OK 응답)
                onResult(true, null)  // 중복되지 않으므로 true, 메시지는 필요 없음
            } else {
                // 닉네임 중복됨 (다른 상태 코드)
                val serverMessage = response.errorBody()?.let { getMessageFromErrorBody(it) }
                    ?: "닉네임 중복을 확인할 수 없습니다."  // errorBody가 null이면 기본 메시지 설정
                onResult(false, serverMessage)  // 중복되었으면 false, 메시지는 서버로부터 받은 메시지
            }
        }
    }

    // registerNickname 메소드: 닉네임 등록
    fun registerNickname(nickname: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = myPageRepository.registerNickname(nickname)
            if (result.isSuccess) {
                onResult(true) // 성공: 등록 성공
            } else {
                Log.e("귯", "닉네임 등록 실패: ${result.exceptionOrNull()}")
                onResult(false)
            }
        }
    }

    //getPresignedUrl 메서드: presigned url 가져오기
    fun getPresignedUrl(imageName: String) {
        viewModelScope.launch {
            myPageRepository.getPresignedImage(imageName)
        }
    }

    //getPresignedUrl 메서드: presigned url 가져오기
    fun registerProfileImage(url: String, image: Byte) {
        viewModelScope.launch {
            myPageRepository.registerProfileImage(url, image)
        }
    }
}
