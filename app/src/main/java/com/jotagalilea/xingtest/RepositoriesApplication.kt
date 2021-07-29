package com.jotagalilea.xingtest

import android.app.Application
import com.jotagalilea.xingtest.di.applicationModule
import com.jotagalilea.xingtest.di.repositoriesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RepositoriesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(listOf(applicationModule, repositoriesModule))
        }
    }
}