package com.reschikov.geekbrains.notes.viewmodel.fragments

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: Repository, private val router:RouterSupportMessage) :
    BaseViewModel<Note?>(){

    private var pendingNote: Note? = null

    @ExperimentalCoroutinesApi
    fun loadNote (noteId: String) {
        launch {
            try {
                pendingNote = repository.getNoteById(noteId)
                setData(pendingNote)
            }catch (e: Throwable){
                setError(e)
            }
        }
    }

    fun saveChanges (note: Note) {
        pendingNote = note
    }

    fun delete(){
        pendingNote?.let {note ->
            note.id?.let {
                launch {
                    try {
                        repository.deleteNote(it)
                    } catch (e: Throwable){
                        setError(e)
                    }
                }
            }
            close()
        }
    }

    private fun close(){
        pendingNote = null
        router.replaceScreen(Screens.ListNotesScreen())
    }

    @ExperimentalCoroutinesApi
    override fun onCleared () {
        pendingNote?.let {
            with(repository){
                launch {
                    try {
                        it.id?.run {
                            saveNote(it)
                        } ?:
                        it.takeUnless {it.title == null && it.note == null }?.let {addNewNote(it)}
                    }catch (e: Throwable){
                        setError(e)
                    }
                }
            }
        }
    }
}