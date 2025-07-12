package com.android.example.blinkit.api

import com.android.example.blinkit.Models.CheckStatus

import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {

    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
    suspend fun checkStatus (
        @HeaderMap headers : Map<String,String>,
        @Path("merchantId") merchantId : String,
        @Path("transactionId") transactionId : String,
    ): retrofit2.Response<CheckStatus>
}