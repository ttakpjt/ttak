package com.ttak.android.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ApiRequestWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // doWork 호출
        Log.d("APIRequestWorker", "doWork is called")

        // API 요청 URL 설정
        val userId = 1 // 임시로 userId를 1로 고정
        val url = "http://192.168.137.1:8080/test/$userId"

        // OkHttpClient로 API 요청 보내기
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            // 요청 실행
            val response: Response = client.newCall(request).execute()

            // 응답 코드가 200이고, message가 SUCCESS일 때 성공 처리
            if (response.isSuccessful) {
                Log.d("ApiRequestWorker", "API 요청 성공: ${response.body?.string()}")
                Result.success()
            } else {
                Log.d("ApiRequestWorker", "API 요청 실패: ${response.code}")
                Result.retry() // 요청이 실패하면 재시도
            }
        } catch (e: Exception) {
            Log.e("ApiRequestWorker", "API 요청 오류", e)
            Result.retry() // 네트워크 오류 시 재시도
        }
    }
}
