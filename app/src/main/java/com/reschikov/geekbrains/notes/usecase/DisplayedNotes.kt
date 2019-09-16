package com.reschikov.geekbrains.notes.usecase

import com.reschikov.geekbrains.notes.view.fragments.adapters.DisplayedNote

interface DisplayedNotes {

    fun getNumberOfNotes() : Int
    fun bindNoteWithView(displayedNote: DisplayedNote, position: Int)
}