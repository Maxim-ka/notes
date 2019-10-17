package com.reschikov.geekbrains.notes.viewmodel.activity

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.User
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import com.reschikov.geekbrains.notes.viewmodel.fragments.BaseViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashViewModel(private val repository: Repository, private val router:RouterSupportMessage) :
        BaseViewModel<User?>(), CoroutineScope {


    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    fun requestUser () {
        launch{
            repository.getCurrentUser()?.let {
                router.replaceScreen(Screens.MainScreen())} ?:
                router.replaceScreen(Screens.AuthScreen())
            }
    }

    @ExperimentalCoroutinesApi
    override fun onCleared () {
        coroutineContext.cancel()
        super .onCleared()
    }
}