package com.reschikov.geekbrains.notes

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.reschikov.geekbrains.notes.di.appModule
import com.reschikov.geekbrains.notes.di.navigatorModule
import com.reschikov.geekbrains.notes.di.softKeyModule
import com.reschikov.geekbrains.notes.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class NoteApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext (this@NoteApp)
            modules(listOf(appModule, navigatorModule, viewModelModule, softKeyModule))
        }
    }
}