package com.reschikov.geekbrains.notes.view.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.reschikov.geekbrains.notes.NoteApp
import com.reschikov.geekbrains.notes.RC_SIGN_IN
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

abstract class BaseActivity :  AppCompatActivity() {

    abstract val navigator: SupportAppNavigator
    private val navigatorHolder: NavigatorHolder = NoteApp.INSTANCE.getNavigatorHolder()

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK){
            finish()
        }
    }
}