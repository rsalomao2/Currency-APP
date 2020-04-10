package com.currencyapp.data.di

import android.app.Application
import com.currencyapp.data.provider.CoroutineContextProvider
import com.currencyapp.data.provider.CoroutineContextProviderImpl
import com.currencyapp.data.provider.StringProvider
import com.currencyapp.data.provider.StringProviderImpl
import com.currencyapp.data.builder.createOkHttpClient
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.with
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

lateinit var okHttpClient: OkHttpClient

fun startKoin(myApplication: Application) {
    StandAloneContext.startKoin(
        listOf(
            networkModule,
            contextModule,
            providerModule
        )
    ) with myApplication

    okHttpClient = createOkHttpClient()
}

private val networkModule = module {
    single { okHttpClient }
}

private val contextModule = module {
    single<CoroutineContextProvider> { CoroutineContextProviderImpl() }
}

private val providerModule = module {
    single<StringProvider> { StringProviderImpl(androidContext()) }
}
