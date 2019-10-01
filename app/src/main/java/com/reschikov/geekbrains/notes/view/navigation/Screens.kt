package com.reschikov.geekbrains.notes.view.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.reschikov.geekbrains.notes.KEY_SCREEN_LIST_NOTES
import com.reschikov.geekbrains.notes.KEY_SCREEN_NOTE
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.RC_SIGN_IN
import com.reschikov.geekbrains.notes.view.activities.MainActivity
import com.reschikov.geekbrains.notes.view.activities.SplashActivity
import com.reschikov.geekbrains.notes.view.fragments.EditorNoteFragment
import com.reschikov.geekbrains.notes.view.fragments.ListNotesFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

sealed class Screens : SupportAppScreen(){

    class ListNotesScreen : Screens() {
        override fun getFragment() = ListNotesFragment()
        override fun getScreenKey() = KEY_SCREEN_LIST_NOTES
    }

    class NoteScreen(private val id: String?) : Screens() {
        override fun getFragment() = EditorNoteFragment.newInstance(id)
        override fun getScreenKey() = KEY_SCREEN_NOTE
    }

    class AuthScreen: Screens() {
        private val providers = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build())

        override fun getActivityIntent(context: Context?): Intent {
            return AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.android_robot)
                    .setTheme(R.style.LoginStyle)
                    .setAvailableProviders(providers)
                    .build()

        }

        fun logIn(activity: Activity){
            activity.startActivityForResult(getActivityIntent(activity.baseContext), RC_SIGN_IN)
        }
    }

    class MainScreen: Screens(){
        override fun getActivityIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    class LogoutScreen: Screens(){

        fun logOut(activity: Activity){
            AuthUI.getInstance()
                .signOut(activity.baseContext)
                .addOnCompleteListener {
                    activity.startActivity(Intent(activity.baseContext, SplashActivity::class.java))
                    activity.finish()
                }
        }
    }
}