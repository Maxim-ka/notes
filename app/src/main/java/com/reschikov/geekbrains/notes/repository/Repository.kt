package com.reschikov.geekbrains.notes.repository

import com.reschikov.geekbrains.notes.repository.model.ColorNote
import com.reschikov.geekbrains.notes.repository.model.Note

object Repository {

    private var notes = mutableListOf(Note(0,"Моя первая заметка" ,
            "Kotlin очень краткий, но при этом выразительный язык" ,
            ColorNote.WHITE),
            Note(0,"Моя вторая заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.YELLOW),
            Note(0,"Моя третья заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.GREEN),
            Note(0,"Моя четвертая заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.BLUE),
            Note(0,"Моя пятая заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.PINK),
            Note(0,"Моя шестая заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.RED),
            Note(0,"Моя седьмая заметка" ,
                    "Kotlin очень краткий, но при этом выразительный язык" ,
                    ColorNote.VIOLET))

    fun getListNotes() = notes

    fun saveNote (note: Note ) {
        addOrReplace(note)
    }

    private fun addOrReplace (note: Note ) {
        for (i in 0 until notes.size) {
            if (notes[i] == note) {
                notes[i] = note
                return
            }
        }
        notes.add(note)
    }
}