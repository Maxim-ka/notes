package com.reschikov.geekbrains.notes

import android.app.Application
import com.reschikov.geekbrains.notes.di.appModule
import com.reschikov.geekbrains.notes.di.navigatorModule
import com.reschikov.geekbrains.notes.di.softKeyModule
import com.reschikov.geekbrains.notes.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TestApp : Application() {


    override fun onCreate () {
        super .onCreate()
        startKoin{androidContext(applicationContext).modules(emptyList()) }
    }
}