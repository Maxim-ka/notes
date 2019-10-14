package com.reschikov.geekbrains.notes.view.activities

import android.graphics.drawable.Drawable

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.reschikov.geekbrains.notes.*
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import com.reschikov.geekbrains.notes.view.navigation.SupportMessage
import com.reschikov.geekbrains.notes.viewmodel.activity.MainViewModel
import org.jetbrains.anko.alert
import org.jetbrains.annotations.TestOnly
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace

class MainActivity : BaseActivity(), Expectative {

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

    private val router: RouterSupportMessage by inject()
    private lateinit var plus : Drawable
    private lateinit var done : Drawable
    private lateinit var fab : FloatingActionButton

    private val viewModel: MainViewModel by inject()

//    private val viewModel: MainViewModel by lazy {
//        ViewModelProviders.of(this).get(MainViewModel::class.java)
//    }

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
                KEY_SCREEN_LIST_NOTES -> {
                    setImageDrawable(plus)
                    tag = R.drawable.ic_add_24dp
                }
                KEY_SCREEN_NOTE -> {
                    setImageDrawable(done)
                    tag = R.drawable.ic_done_white_24dp
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        viewModel.currentScreen ?: router.replaceScreen(Screens.ListNotesScreen())
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
                router.replaceScreen(Screens.NoteScreen(null))
            }
            KEY_SCREEN_NOTE -> {
                router.replaceScreen(Screens.ListNotesScreen())
            }
        }
    }

    override fun onCreateOptionsMenu (menu: Menu?): Boolean =
            MenuInflater( this ).inflate(R.menu.menu_main_activity, menu).let { true }

    override fun onOptionsItemSelected (item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.logout -> showLogoutDialog().let { true }
                else -> false
            }

    private fun showLogoutDialog(){
        alert {
            titleResource = R.string.logout_dialog_title
            messageResource = R.string.logout_dialog_message
            positiveButton(R.string.dialog_ok) {
                onLogout()
                it.dismiss()
            }
        }.show()
    }

    private fun onLogout(){
        router.replaceScreen(Screens.LogoutScreen())
    }

    override fun onBackPressed() {
        if (viewModel.currentScreen?.screenKey == KEY_SCREEN_NOTE){
            router.replaceScreen(Screens.ListNotesScreen())
            return
        }
        super.onBackPressed()
    }

    override fun toBegin() {
        progress_bar.run {
            visibility = View.VISIBLE
        }
    }

    override fun toFinish() {
        progress_bar.run {
            visibility = View.INVISIBLE
        }
    }
}
