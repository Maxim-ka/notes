package com.reschikov.geekbrains.notes

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Cicerone
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import timber.log.Timber


class NoteApp : Application() {

    companion object{
        lateinit var INSTANCE: NoteApp
    }

    private lateinit var cicerone: Cicerone<Router>

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        INSTANCE = this
        cicerone = Cicerone.create()
    }

    fun getNavigatorHolder(): NavigatorHolder {
        return cicerone.navigatorHolder
    }

    fun getRouter(): RouterSupportMessage {
        return RouterSupportMessage(cicerone.router)
    }
}