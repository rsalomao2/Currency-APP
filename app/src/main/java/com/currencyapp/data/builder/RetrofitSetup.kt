package com.currencyapp.data.builder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://hiring.revolut.codes/api/android/"

internal fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    val okHttpClient = OkHttpClient.Builder()
    okHttpClient.connectTimeout(10, TimeUnit.SECONDS)
    okHttpClient.readTimeout(10, TimeUnit.SECONDS)
    okHttpClient.addInterceptor(logging)
    return okHttpClient.build()
}

internal inline fun <reified T> createRetrofit(okHttpClient: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    return retrofit.create(T::class.java)
}
