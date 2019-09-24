package com.reschikov.geekbrains.notes

import com.reschikov.geekbrains.notes.repository.model.ColorNote
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_TIME_FORMAT = "dd.MMM.yy HH:mm"

fun getDateTime(date: Date): String{
    return SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(date)
}

fun getResourceColor(colorNote: ColorNote): Int {
    return when (colorNote) {
        ColorNote.WHITE -> R.color.color_white
        ColorNote.VIOLET -> R.color.color_violet
        ColorNote.YELLOW -> R.color.color_yellow
        ColorNote.RED -> R.color.color_red
        ColorNote.PINK -> R.color.color_pink
        ColorNote.GREEN -> R.color.color_green
        ColorNote.BLUE -> R.color.color_blue
    }
}