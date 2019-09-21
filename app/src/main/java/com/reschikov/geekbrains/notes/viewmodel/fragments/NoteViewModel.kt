package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note

class NoteViewModel(private val repository: Repository = Repository) : ViewModel() {

    private var pendingNote: Note? = null

    fun saveChanges (note: Note) {
        pendingNote = note
    }

    override fun onCleared () {
        if (pendingNote != null ) {
            repository.saveNote(pendingNote!!)
        }
    }
}