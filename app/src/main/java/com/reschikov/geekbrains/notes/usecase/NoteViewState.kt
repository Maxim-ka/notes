package com.reschikov.geekbrains.notes.usecase

import com.reschikov.geekbrains.notes.repository.model.Note

class NoteViewState(note: Note? = null, error: Throwable? = null ) :
        BaseViewState<Note?>(note, error) {
}