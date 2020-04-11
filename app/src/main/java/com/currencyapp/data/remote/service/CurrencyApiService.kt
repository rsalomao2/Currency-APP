package com.currencyapp.data.remote.service

import com.currencyapp.data.remote.dto.CurrencyResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("latest")
    suspend fun getApiLatestCurrency(@Query("base") base: String): CurrencyResponseDto
}
