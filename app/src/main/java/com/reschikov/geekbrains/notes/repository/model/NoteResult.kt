package com.reschikov.geekbrains.notes.repository.model

import com.reschikov.geekbrains.notes.NoteApp
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.view.navigation.Screens

sealed class NoteResult {

    data class Success <out T>(val data: T) : NoteResult()
    data class Error (val error: Throwable) : NoteResult(){
        fun renderError (error: Throwable) {
            when (error) {
                is NoAuthException -> NoteApp.INSTANCE.getRouter().replaceScreen(Screens.AuthScreen())
                else -> error.message?.let {
                    NoteApp.INSTANCE.getRouter().sendMessage(it)
                }
            }
        }
    }
}