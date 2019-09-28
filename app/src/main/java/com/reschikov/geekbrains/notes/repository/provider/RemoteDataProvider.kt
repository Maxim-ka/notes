package com.reschikov.geekbrains.notes.repository.provider

import androidx.lifecycle.LiveData
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult

interface RemoteDataProvider {

    fun subscribeToAllNotes(): LiveData<NoteResult>
    fun getNoteById (id: String): LiveData<NoteResult>
    fun addNewNotes(note: Note): LiveData<NoteResult>
    fun saveChangesNote (note: Note): LiveData<NoteResult>
}