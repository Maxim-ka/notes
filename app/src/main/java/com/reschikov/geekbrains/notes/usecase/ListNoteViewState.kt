package com.reschikov.geekbrains.notes.usecase

import com.reschikov.geekbrains.notes.repository.model.Note

class ListNoteViewState (notes: MutableList<Note>? = null, error: Throwable? = null) :
        BaseViewState<MutableList<Note>?>(notes, error) {

}