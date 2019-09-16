package com.reschikov.geekbrains.notes.view.fragments.adapters

import com.reschikov.geekbrains.notes.repository.model.Note

interface DisplayedNote {
    fun show(note: Note)
}