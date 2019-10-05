package com.reschikov.geekbrains.notes.viewmodel.activity

import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.NoteApp
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.view.navigation.Screens

class SplashViewModel(private val repository: Repository = Repository) :
        ViewModel() {

    fun requestUser () {
        repository.getCurrentUser().observeForever {
            it?.let {
                NoteApp.INSTANCE.getRouter().replaceScreen(Screens.MainScreen())
            } ?:
                NoteApp.INSTANCE.getRouter().replaceScreen(Screens.AuthScreen())
            }
    }
}