package com.reschikov.geekbrains.notes

import android.content.Context
import androidx.core.content.ContextCompat
import com.reschikov.geekbrains.notes.repository.model.ColorNote
import java.text.SimpleDateFormat
import java.util.*

const val RC_SIGN_IN = 458
const val KEY_SCREEN_LIST_NOTES = "key screen list notes"
const val KEY_SCREEN_NOTE = "key screen note"
private const val DATE_TIME_FORMAT = "dd.MMM.yy HH:mm"


fun Long.formatDateTime(): String{
    return SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(this)
}

fun ColorNote.getColor(context: Context):Int{
    return ContextCompat.getColor(context, this.getResourceColor())
}

fun ColorNote.getResourceColor(): Int {
    return when (this) {
        ColorNote.WHITE -> R.color.color_white
        ColorNote.VIOLET -> R.color.color_violet
        ColorNote.YELLOW -> R.color.color_yellow
        ColorNote.RED -> R.color.color_red
        ColorNote.PINK -> R.color.color_pink
        ColorNote.GREEN -> R.color.color_green
        ColorNote.BLUE -> R.color.color_blue
    }
}