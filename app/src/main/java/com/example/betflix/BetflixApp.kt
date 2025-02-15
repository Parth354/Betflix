package com.example.betflix

import android.app.Application
import org.koin.core.context.startKoin

class BetflixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}
