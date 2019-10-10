package com.reschikov.geekbrains.notes.repository

import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.provider.RemoteDataProvider

class Repository(private val remoteProvider: RemoteDataProvider) {

    fun getNotes() = remoteProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteProvider.saveChangesNote(note)
    fun getNoteById(id: String) = remoteProvider.getNoteById(id)
    fun addNewNote(note: Note) = remoteProvider.addNewNotes(note)
    fun removeItem(id: String) = remoteProvider.removeItem(id)
    fun deleteNote(id: String)= remoteProvider.deleteNote(id)
    fun getCurrentUser () = remoteProvider.getCurrentUser()
}