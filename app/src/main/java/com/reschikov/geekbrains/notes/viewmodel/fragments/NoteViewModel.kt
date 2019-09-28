package com.reschikov.geekbrains.notes.viewmodel.fragments

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.usecase.NoteViewState

class NoteViewModel(private val repository: Repository = Repository) :
    BaseViewModel<Note?, NoteViewState>(){

    private var pendingNote: Note? = null

    fun loadNote (noteId: String) {
        repository.getNoteById(noteId).observeForever { noteResult ->
            noteResult?.let {
                with(viewStateLiveData){
                    value = when (it) {
                        is NoteResult.Success<*> ->
                            NoteViewState(note = it.data as? Note)
                        is NoteResult.Error ->
                            NoteViewState(error = it.error)
                    }
                }
            }
        }
    }

    fun saveChanges (note: Note) {
        pendingNote = note
    }

    override fun onCleared () {
        pendingNote?.let {
            with(repository){
                it.id?.run {saveNote(it) } ?: addNewNote(it)
            }
        }
    }
}