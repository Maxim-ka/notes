package com.reschikov.geekbrains.notes.viewmodel.fragments

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.usecase.NoteViewState
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens

class NoteViewModel(private val repository: Repository, private val router:RouterSupportMessage) :
    BaseViewModel<Note?, NoteViewState>(){

    private var pendingNote: Note? = null

    fun loadNote (noteId: String) {
        repository.getNoteById(noteId).observeForever { noteResult ->
            noteResult?.let {
                when(it){
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(note = it.data as? Note)
                    is NoteResult.Error ->
                        it.renderError(it.error)
                }
            }
        }
    }

    fun saveChanges (note: Note) {
        pendingNote = note
    }

    fun delete(){
        pendingNote?.let {
            if (it.id != null) {
                repository.deleteNote(it.id).observeForever{result ->
                    when(result){
                        is NoteResult.Error -> result.renderError(result.error)
                        else -> close()
                    }
                }
            } else close()
        }
    }

    private fun close(){
        pendingNote = null
        router.replaceScreen(Screens.ListNotesScreen())
    }

    override fun onCleared () {
        pendingNote?.let {
            with(repository){
                it.id?.run {saveNote(it) } ?:
                it.takeUnless {it.title == null && it.note == null }?.let {addNewNote(it)}
            }
        }
    }
}