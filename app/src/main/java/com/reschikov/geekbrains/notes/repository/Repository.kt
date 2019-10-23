package com.reschikov.geekbrains.notes.repository

import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.provider.RemoteDataProvider

class Repository(private val remoteProvider: RemoteDataProvider) {

    fun getNotes() = remoteProvider.subscribeToAllNotes()
    suspend fun saveNote(note: Note) = remoteProvider.saveChangesNote(note)
    suspend fun getNoteById(id: String) = remoteProvider.getNoteById(id)
    suspend fun addNewNote(note: Note) = remoteProvider.addNewNotes(note)
    suspend fun removeItem(id: String) = remoteProvider.removeItem(id)
    suspend fun deleteNote(id: String)= remoteProvider.deleteNote(id)
    suspend fun getCurrentUser () = remoteProvider.getCurrentUser()
}