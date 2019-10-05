package com.reschikov.geekbrains.notes.repository

import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.provider.FireStoreProvider
import com.reschikov.geekbrains.notes.repository.provider.RemoteDataProvider

object Repository {

    private val remoteProvider: RemoteDataProvider = FireStoreProvider()

    fun getNotes() = remoteProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteProvider.saveChangesNote(note)
    fun getNoteById(id: String) = remoteProvider.getNoteById(id)
    fun addNewNote(note: Note) = remoteProvider.addNewNotes(note)
    fun deleteNote(note: Note) = remoteProvider.deleteNote(note)
    fun getCurrentUser () = remoteProvider.getCurrentUser()
}