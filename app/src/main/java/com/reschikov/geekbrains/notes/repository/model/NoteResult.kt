package com.reschikov.geekbrains.notes.repository.model


sealed class NoteResult {

    data class Success(val data: List<Note>) : NoteResult()
    data class Error (val error: Throwable) : NoteResult()
}