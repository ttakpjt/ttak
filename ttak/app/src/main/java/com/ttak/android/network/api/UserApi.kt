package com.ttak.android.network.api

import com.ttak.android.domain.model.ApiResponse
import com.ttak.android.domain.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("user/search")
    suspend fun searchUsers(@Query("nickname") query: String): Response<ApiResponse<List<User>>>
    @POST("friends/{followingId}")
    suspend fun addFriend(@Path("followingId") followingId: Long): Response<Unit>
}