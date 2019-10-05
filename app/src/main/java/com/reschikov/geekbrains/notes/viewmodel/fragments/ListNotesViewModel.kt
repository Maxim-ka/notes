package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.usecase.ListNoteViewState

class ListNotesViewModel(val repository: Repository = Repository): BaseViewModel<MutableList<Note>?, ListNoteViewState>() {

    private val repositoryNotes: LiveData<NoteResult> = repository.getNotes()
    private val notesObserver = Observer<NoteResult> { noteResult ->
        noteResult?.let {
            when (it) {
                is NoteResult.Success<*> -> {
                    viewStateLiveData.value = ListNoteViewState(notes = it.data as MutableList<Note>?)
                }
                is NoteResult.Error -> it.renderError(it.error)
            }
        }
    }

    init {
        repositoryNotes.observeForever(notesObserver)
    }

    fun deleteNote(note: Note){
        repository.deleteNote(note)
    }

    override fun onCleared () {
        repositoryNotes.removeObserver(notesObserver)
    }
}