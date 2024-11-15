//package com.ttak.android.network.implementation
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import android.util.Log
//import com.ttak.android.data.repository.MyPageRepository
//import com.ttak.android.domain.model.MyPageResponse
//import com.ttak.android.domain.model.NicknameRequest
//import com.ttak.android.network.api.MyPageApi
//
//class MyPageApiImpl (
//    private val api: MyPageApi
//    ) : MyPageRepository {
//
//        // 닉네임 중복 검사
//        override suspend fun checkNickname(nickname: String): Result<MyPageResponse> = handleApiResponse {
//            val nicknameRequest = NicknameRequest(nickname)
//            val response = api.checkNickname(nicknameRequest)
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    Result.success(it)
//                } ?: Result.failure(Exception("Response body is null"))
//            } else {
//                Result.failure(Exception("API 호출 실패"))
//            }
//        }
//
//        // 닉네임 등록
//        override suspend fun registerNickname(nickname: String): Result<MyPageResponse> = handleApiResponse {
//            val nicknameRequest = NicknameRequest(nickname)
//            val response = api.registerNickname(nicknameRequest)
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    Result.success(it)
//                } ?: Result.failure(Exception("Response body is null"))
//            } else {
//                Result.failure(Exception("API 호출 실패"))
//            }
//        }
//
//        // 공통 예외 처리
//        private suspend fun <T> handleApiResponse(apiCall: suspend () -> Result<T>): Result<T> {
//            return withContext(Dispatchers.IO) {
//                try {
//                    apiCall()
//                } catch (e: Exception) {
//                    Log.e("API", "API 요청 중 예외 발생", e)
//                    Result.failure(e)
//                }
//            }
//        }
//    }

package com.ttak.android.network.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.ttak.android.data.repository.MyPageRepository
import com.ttak.android.domain.model.MyPageResponse
import com.ttak.android.domain.model.NicknameRequest
import com.ttak.android.network.api.MyPageApi
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MyPageApiImpl(
    private val api: MyPageApi
) : MyPageRepository {

    // 닉네임 중복 검사
    override suspend fun checkNickname(nickname: String): Response<MyPageResponse> =
        try {
            val response = api.checkNickname(NicknameRequest(nickname))
            if (response.isSuccessful) {
                response  // 200 OK 응답일 경우 응답 그대로 반환
            } else {
                // 실패한 경우, 에러 메시지를 포함한 Response 반환
                Response.error(
                    response.code(),
                    response.errorBody() ?: "닉네임 중복 확인 실패".toResponseBody()
                )
            }
        } catch (e: Exception) {
            // 예외 발생 시, 예외 메시지를 포함한 Response 반환
            Response.error(500, "닉네임 중복 확인 중 예외 발생: ${e.message}".toResponseBody())
        }

    // 닉네임 등록
    override suspend fun registerNickname(nickname: String): Result<MyPageResponse> =
        handleApiResponse {
            val nicknameRequest = NicknameRequest(nickname)

            // Request 로깅
            Log.d("API", "=== Register Nickname Request ===")
            Log.d("API", "Endpoint: /register-nickname")
            Log.d("API", "Request Body: $nicknameRequest")

            val response = api.registerNickname(nicknameRequest)

            // Response 로깅
            Log.d("API", "=== Register Nickname Response ===")
            Log.d("API", "Response Code: ${response.code()}")
            Log.d("API", "Response Headers: ${response.headers()}")
            Log.d("API", "Response Body: ${response.body()}")

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API 호출 실패"))
            }
        }

    // presigned url 가져오기
    override suspend fun getPresignedImage(imageName: String): Response<MyPageResponse> =
        try {
            val response = api.getPresignedUrl(imageName)
            if (response.isSuccessful) {
                Log.d("귯", "${response.body()}")
                response  // 200 OK 응답일 경우 응답 그대로 반환
            } else {
                // 실패한 경우, 에러 메시지를 포함한 Response 반환
                Response.error(
                    response.code(),
                    response.errorBody() ?: "presigned url 가져오기 실패".toResponseBody()
                )
            }
        } catch (e: Exception) {
            // 예외 발생 시, 예외 메시지를 포함한 Response 반환
            Response.error(500, "닉네임 중복 확인 중 예외 발생: ${e.message}".toResponseBody())
        }


    // 프로필 사진 등록하기
    override suspend fun registerProfileImage(url: String, image: Byte): Response<MyPageResponse> =
        try {
            val response = api.registerProfileImage(url, image)
            if (response.isSuccessful) {
                response  // 200 OK 응답일 경우 응답 그대로 반환
            } else {
                // 실패한 경우, 에러 메시지를 포함한 Response 반환
                Response.error(
                    response.code(),
                    response.errorBody() ?: "사진 등록 실패".toResponseBody()
                )
            }
        } catch (e: Exception) {
            // 예외 발생 시, 예외 메시지를 포함한 Response 반환
            Response.error(500, "닉네임 중복 확인 중 예외 발생: ${e.message}".toResponseBody())
        }

    // 공통 예외 처리
    private suspend fun <T> handleApiResponse(apiCall: suspend () -> Result<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                apiCall()
            } catch (e: Exception) {
                Log.e("API", "API 요청 중 예외 발생", e)
                Result.failure(e)
            }
        }
    }
}