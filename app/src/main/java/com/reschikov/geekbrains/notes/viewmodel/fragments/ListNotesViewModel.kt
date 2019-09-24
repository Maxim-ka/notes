package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.usecase.StateListNote

class ListNotesViewModel(repository: Repository = Repository): ViewModel() {

    private val mutableLiveData = MutableLiveData<StateListNote>()

    init {
        mutableLiveData.value = StateListNote(repository.getListNotes())
    }

    fun getViewStateListNote() : LiveData<StateListNote> {
        return mutableLiveData
    }
}