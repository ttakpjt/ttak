package com.ttak.android.network.api

import com.ttak.android.domain.model.CountResponse
import retrofit2.Response
import retrofit2.http.POST

interface ObserverApi {

//    //
//    @get("histroy/pick-rank")
//    suspend fun signIn(): Response<SignInResponse>


    suspend fun updateMyStatus(state: Int): Result<Unit>
}