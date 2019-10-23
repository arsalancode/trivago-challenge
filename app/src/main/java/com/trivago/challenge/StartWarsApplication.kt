package com.trivago.challenge

import android.app.Application
import com.trivago.challenge.dependencies.BaseDependencies
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StartWarsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            // Android context
            androidContext(this@StartWarsApplication)
            // modules
            modules(BaseDependencies.networkingModule)
        }
    }
}