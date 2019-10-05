package com.reschikov.geekbrains.notes.view.navigation

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

class RouterSupportMessage(private val router: Router) : Router(){

    override fun replaceScreen(screen: Screen?) {
        router.replaceScreen(screen)
    }

    fun sendMessage(message: String){
        executeCommands(SupportMessage(message))
    }
}