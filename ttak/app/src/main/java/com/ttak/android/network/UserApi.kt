package com.ttak.android.network

import com.ttak.android.domain.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @GET("users/search")
    suspend fun searchUsers(@Query("query") query: String): List<User>

    @POST("friends/add")
    suspend fun addFriend(@Query("userId") userId: String): Response<Unit>

}