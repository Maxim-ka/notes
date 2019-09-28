package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reschikov.geekbrains.notes.usecase.BaseViewState

open class BaseViewModel <T, S: BaseViewState<T>> : ViewModel() {
    open val viewStateLiveData = MutableLiveData<S>()
    open fun getViewState (): LiveData<S> = viewStateLiveData
}