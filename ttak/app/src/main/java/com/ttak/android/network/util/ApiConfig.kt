package com.ttak.android.network.util


import android.content.Context
import android.util.Log
import com.ttak.android.network.api.MemberApi
import com.ttak.android.network.api.MyPageApi
import com.ttak.android.network.api.UserApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    const val BASE_URL = "https://k11a509.p.ssafy.io/api/"  // 실제 서버 URL로 변경
    const val TIMEOUT_SECONDS = 30L

    // 로그 인터셉터 설정
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 사용자 ID를 추가하는 인터셉터 설정
    private fun userIdInterceptor(context: Context) = Interceptor { chain ->
        val userId = UserPreferences(context).getUserId()  // UserPreferences에서 userId 가져오기

        val requestBuilder = chain.request().newBuilder()

        // userId가 있는지 확인하고 헤더 추가
        if (userId != null) {
            requestBuilder.addHeader("user", userId.toString())
            Log.d("닉네임", "userId 헤더 추가됨: $userId")
        } else {
            chain.request()
        }

        // 최종 요청
        val request = requestBuilder.build()

        // 전체 요청 정보 로그 출력
        Log.d("닉네임", "요청 URL: ${request.url}")
        Log.d("닉네임", "요청 헤더: ${request.headers}")

        chain.proceed(request)
    }

    // OkHttpClient 설정
    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(userIdInterceptor(context))  // userId 인터셉터 추가
            .build()
    }

    // Retrofit 생성
    fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))  // 클라이언트 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    // MemberApi 인스턴스 생성
    fun createMemberApi(context: Context): MemberApi {
        return createRetrofit(context).create(MemberApi::class.java)
    }

    // UserApi 인스턴스 생성
    fun createUserApi(context: Context): UserApi {
        return createRetrofit(context).create(UserApi::class.java)
    }

    // testApi 인스턴스 생성
    fun createTestApi(context: Context): MemberApi {
        return createRetrofit(context).create(MemberApi::class.java)
    }

    // myPageApi 생성 메소드
    fun createMyPageApi(context: Context): MyPageApi {
        return createRetrofit(context).create(MyPageApi::class.java)
    }
}