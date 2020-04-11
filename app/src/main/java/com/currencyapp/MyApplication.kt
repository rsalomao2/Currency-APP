package com.currencyapp

import android.app.Application
import com.currencyapp.data.di.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this)
    }
}
