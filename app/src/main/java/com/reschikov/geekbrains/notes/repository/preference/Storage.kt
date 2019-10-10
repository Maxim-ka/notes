package com.reschikov.geekbrains.notes.repository.preference

import android.content.Context
import java.util.*


private const val USER = "user"
private const val ANONYMOUS = "anonymous"

class Storage(private val context: Context) {

    fun toAssign(): String{
        val preferences = context.getSharedPreferences(USER, Context.MODE_PRIVATE)
        var name = preferences.getString(ANONYMOUS, null)
        if (name == null){
            name = UUID.randomUUID().toString()
            preferences.edit().putString(ANONYMOUS, name).apply()
        }
        return name
    }
}