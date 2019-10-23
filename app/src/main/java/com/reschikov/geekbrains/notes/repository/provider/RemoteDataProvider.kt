package com.reschikov.geekbrains.notes.repository.provider

import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.repository.model.User
import kotlinx.coroutines.channels.ReceiveChannel

interface RemoteDataProvider {

    fun subscribeToAllNotes(): ReceiveChannel<NoteResult>
    suspend fun getNoteById (id: String): Note
    suspend fun addNewNotes(note: Note)
    suspend fun saveChangesNote (note: Note)
    suspend fun removeItem(id: String): ReceiveChannel<NoteResult>
    suspend fun deleteNote(id: String)
    suspend fun getCurrentUser (): User?
}