package com.reschikov.geekbrains.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private val mutableLiveData = MutableLiveData<String>()
    private var count = 0

    init {
        mutableLiveData.value = "Здравствуйте"
    }

    fun getViewState() : LiveData<String> {
        return mutableLiveData
    }

    fun getPressed() {
        mutableLiveData.value = (++count).toString()
    }
}