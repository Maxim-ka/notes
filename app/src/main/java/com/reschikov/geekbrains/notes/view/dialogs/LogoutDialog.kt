package com.reschikov.geekbrains.notes.view.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.reschikov.geekbrains.notes.NoteApp
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.view.navigation.Screens

class LogoutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_dialog_ok) { _, _ ->
                    onLogout()
                }
                .create()
    }

    private fun onLogout(){
        NoteApp.INSTANCE.getRouter().replaceScreen(Screens.LogoutScreen())
        dismiss()
    }
}