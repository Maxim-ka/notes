package com.reschikov.geekbrains.notes.viewmodel.activity

import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens

class SplashViewModel(private val repository: Repository, private val router:RouterSupportMessage) :
        ViewModel() {

    fun requestUser () {
        repository.getCurrentUser().observeForever {
            it?.let {
                router.replaceScreen(Screens.MainScreen())
            } ?:
                router.replaceScreen(Screens.AuthScreen())
            }
    }
}