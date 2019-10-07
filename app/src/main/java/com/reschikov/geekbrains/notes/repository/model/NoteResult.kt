package com.reschikov.geekbrains.notes.repository.model

import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import org.koin.core.KoinComponent
import org.koin.core.inject

sealed class NoteResult : KoinComponent {

    data class Success <out T>(val data: T) : NoteResult()
    data class Error (val error: Throwable) : NoteResult(){

        private val router: RouterSupportMessage by inject()

        fun renderError (error: Throwable) {
            when (error) {
                is NoAuthException -> router.replaceScreen(Screens.AuthScreen())
                else -> error.message?.let {
                    router.sendMessage(it)
                }
            }
        }
    }
}