package com.reschikov.geekbrains.notes.viewmodel.fragments

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListNotesViewModel(val repository: Repository) : BaseViewModel<List<Note>?>() {

    private val channelNoteResult = repository.getNotes()

    init {
        launch {
            channelNoteResult.consumeEach {
                when (it) {
                    is NoteResult.Success -> setData(it.data)
                    is NoteResult.Error ->  setError(it.error)
                }
            }
        }
    }

    fun deleteNote(note: Note){
        note.id?.let {
            launch {
                repository.removeItem(it) }
            }
    }

    override fun onCleared () {
        channelNoteResult.cancel()
    }
}