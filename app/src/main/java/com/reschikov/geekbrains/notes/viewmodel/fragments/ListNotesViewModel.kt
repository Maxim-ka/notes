package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.usecase.DisplayedNotes
import com.reschikov.geekbrains.notes.usecase.StateListNote
import com.reschikov.geekbrains.notes.view.fragments.adapters.DisplayedNote

class ListNotesViewModel: ViewModel(), DisplayedNotes {

    private val mutableLiveData = MutableLiveData<StateListNote>()

    init {
        mutableLiveData.value = StateListNote(Repository().createListNotes())
    }

    fun getViewStateListNote() : LiveData<StateListNote> {
        return mutableLiveData
    }

    override fun getNumberOfNotes(): Int {
        return mutableLiveData.value?.list?.size ?: 0
    }

    override fun bindNoteWithView(displayedNote: DisplayedNote, position: Int) {
         displayedNote.show(mutableLiveData.value!!.list[position])
    }
}