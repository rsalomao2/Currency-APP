package com.currencyapp.data.di

import com.currencyapp.data.builder.createRetrofit
import com.currencyapp.data.remote.repository.CurrencyRepository
import com.currencyapp.data.remote.repository.CurrencyRepositoryImpl
import com.currencyapp.data.remote.service.CurrencyApiService
import com.currencyapp.presentation.currency.CurrencyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

fun injectCurrencyModules() = loadKoinModule

private val loadKoinModule by lazy {
    StandAloneContext.loadKoinModules(
        serviceModule,
        repositoryModule,
        viewModelModule
    )
}

private val viewModelModule: Module = module {
    viewModel { CurrencyViewModel(repository = get()) }
}

private val repositoryModule: Module = module {
    single<CurrencyRepository> { CurrencyRepositoryImpl(context = androidContext(), service = get(), contextProvider = get()) }
}

private val serviceModule: Module = module {
    single { createRetrofit<CurrencyApiService>(okHttpClient = get()) }
}
