package com.reschikov.geekbrains.notes.view.activities

import android.os.Handler
import com.reschikov.geekbrains.notes.*
import com.reschikov.geekbrains.notes.view.navigation.Screens
import com.reschikov.geekbrains.notes.viewmodel.activity.SplashViewModel
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Replace

private const val START_DELAY = 1_000L

class SplashActivity : BaseActivity() {

    private val model: SplashViewModel by viewModel()

    override val navigator = object : SupportAppNavigator(this, R.id.frame_master){
        override fun activityReplace(command: Replace?) {
            command?.let {
                when(it.screen){
                    is Screens.AuthScreen -> (it.screen as Screens.AuthScreen).logIn(this@SplashActivity)
                    is Screens.MainScreen -> {
                        startActivity(intentFor<MainActivity>().newTask())
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ model.requestUser() }, START_DELAY)
    }
}