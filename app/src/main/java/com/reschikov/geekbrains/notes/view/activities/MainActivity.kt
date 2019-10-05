package com.reschikov.geekbrains.notes.view.activities

import android.graphics.drawable.Drawable

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.reschikov.geekbrains.notes.*
import com.reschikov.geekbrains.notes.view.dialogs.LogoutDialog
import com.reschikov.geekbrains.notes.view.navigation.Screens
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import com.reschikov.geekbrains.notes.view.navigation.SupportMessage
import com.reschikov.geekbrains.notes.viewmodel.activity.MainViewModel
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace

private const val TAG_LOGOUT = "tag logout"

class MainActivity : BaseActivity() {

    override val navigator = object : SupportAppNavigator(this, R.id.frame_master){
        override fun applyCommand(command: Command?) {
            (command as? SupportMessage)?.let {
                showMessage(it.message)
            } ?: super.applyCommand(command)
        }

        override fun activityReplace(command: Replace?) {
            command?.let {
                when(it.screen){
                    is Screens.AuthScreen -> (it.screen as Screens.AuthScreen).logIn(this@MainActivity)
                    is Screens.LogoutScreen -> (it.screen as Screens.LogoutScreen).logOut(this@MainActivity)
                    else -> {
                        fragmentReplace(it)
                        viewModel.currentScreen = it.screen as Screens
                        setImageFabForTag()
                    }
                }
            }
        }
    }

    private lateinit var plus : Drawable
    private lateinit var done : Drawable
    private lateinit var fab : FloatingActionButton

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun showMessage(message: String){
        Snackbar.make(bottomAppBar, message, Snackbar.LENGTH_INDEFINITE)
            .apply {
                anchorView = fab
                show()
            }
    }

    private fun setImageFabForTag() {
        fab.apply {
            when(viewModel.currentScreen?.screenKey){
                KEY_SCREEN_LIST_NOTES -> setImageDrawable(plus)
                KEY_SCREEN_NOTE -> setImageDrawable(done)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        viewModel.currentScreen ?: NoteApp.INSTANCE.getRouter().replaceScreen(Screens.ListNotesScreen())
        initFab()
    }

    private fun initFab(){
        plus = ContextCompat.getDrawable(baseContext, R.drawable.ic_add_24dp)!!
        done = ContextCompat.getDrawable(baseContext, R.drawable.ic_done_white_24dp)!!
        fab = fb_fab
        fab.setOnClickListener {onClick()}
        setImageFabForTag()
    }

    private fun onClick() {
        when (viewModel.currentScreen?.screenKey) {
            KEY_SCREEN_LIST_NOTES -> {
                NoteApp.INSTANCE.getRouter().replaceScreen(Screens.NoteScreen(null))
            }
            KEY_SCREEN_NOTE -> {
                NoteApp.INSTANCE.getRouter().replaceScreen(Screens.ListNotesScreen())
            }
        }
    }

    override fun onCreateOptionsMenu (menu: Menu?): Boolean =
            MenuInflater( this ).inflate(R.menu.menu, menu).let { true }

    override fun onOptionsItemSelected (item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.logout -> showLogoutDialog().let { true }
                else -> false
            }

    private fun showLogoutDialog() {
        supportFragmentManager.findFragmentByTag(TAG_LOGOUT) ?:
        LogoutDialog().show(supportFragmentManager, TAG_LOGOUT)
    }

    override fun onBackPressed() {
        if (viewModel.currentScreen?.screenKey == KEY_SCREEN_NOTE){
            NoteApp.INSTANCE.getRouter().replaceScreen(Screens.ListNotesScreen())
            return
        }
        super.onBackPressed()
    }
}
