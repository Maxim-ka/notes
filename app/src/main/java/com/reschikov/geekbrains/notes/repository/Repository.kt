package com.reschikov.geekbrains.notes.repository

import com.reschikov.geekbrains.notes.repository.model.ColorNote
import com.reschikov.geekbrains.notes.repository.model.Note

object Repository {

    private var notes = mutableListOf(Note(title = "Моя первая заметка"),
            Note(title = "Моя вторая заметка", color = ColorNote.YELLOW),
            Note(title = "Моя третья заметка", color = ColorNote.GREEN),
            Note(title = "Моя четвертая заметка", color = ColorNote.BLUE),
            Note(title = "Моя пятая заметка", color = ColorNote.PINK),
            Note(title = "Моя шестая заметка", color = ColorNote.RED),
            Note(title = "Моя седьмая заметка", color = ColorNote.VIOLET))

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