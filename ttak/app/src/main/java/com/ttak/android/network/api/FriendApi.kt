//package com.ttak.android.network.api
//
//import com.ttak.android.domain.model.ApiResponse
//import com.ttak.android.domain.model.FriendStory
//import retrofit2.Response
//import retrofit2.http.GET
//
//interface FriendApi {
//    @GET("/friends/live")
//    suspend fun getFriendsList(): Response<ApiResponse<List<FriendStory>>>
//}

package com.ttak.android.network.api

import com.ttak.android.domain.model.FriendStory
import com.ttak.android.domain.model.response.FriendResponse
import retrofit2.Response
import retrofit2.http.GET

interface FriendApi {
    @GET("friends/live")
    suspend fun getFriendsList(): Response<FriendResponse>
}