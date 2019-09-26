package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.Observer
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.usecase.ListNoteViewState

class ListNotesViewModel(repository: Repository = Repository): BaseViewModel<MutableList<Note>?, ListNoteViewState>() {

    private val repositoryNotes = repository.getNotes()
    private val notesObserver = Observer<NoteResult> { noteResult ->
        noteResult?.let {
            when (it) {
                is NoteResult.Success<*> -> {
                    viewStateLiveData.value = ListNoteViewState(notes = it.data as? MutableList<Note>)
                }
                is NoteResult.Error -> {
                    viewStateLiveData.value = ListNoteViewState(error = it.error)
                }
            }
        }
    }

    init {
        viewStateLiveData.value = ListNoteViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    override fun onCleared () {
        repositoryNotes.removeObserver(notesObserver)
    }
}