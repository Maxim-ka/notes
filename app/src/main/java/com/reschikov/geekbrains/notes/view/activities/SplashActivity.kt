package com.reschikov.geekbrains.notes.view.activities

import android.os.Handler
import androidx.lifecycle.ViewModelProviders
import com.reschikov.geekbrains.notes.*
import com.reschikov.geekbrains.notes.view.navigation.Screens
import com.reschikov.geekbrains.notes.viewmodel.activity.SplashViewModel
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Replace

private const val START_DELAY = 1_000L

class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by lazy {
        ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override val navigator = object : SupportAppNavigator(this, R.id.frame_master){
        override fun activityReplace(command: Replace?) {
            command?.let {
                when(it.screen){
                    is Screens.AuthScreen -> (it.screen as Screens.AuthScreen).logIn(this@SplashActivity)
                    is Screens.MainScreen -> {
                        startActivity((it.screen as Screens.MainScreen).getActivityIntent(this@SplashActivity))
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ viewModel.requestUser() }, START_DELAY)
    }
}