package com.reschikov.geekbrains.notes.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.reschikov.geekbrains.notes.viewmodel.fragments.BaseViewModel
import com.reschikov.geekbrains.notes.usecase.BaseViewState

abstract class BaseFragment<T, S : BaseViewState<T>>(layoutId: Int) : Fragment(layoutId) {

    abstract val viewModel: BaseViewModel<T, S>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getViewState().observe(this, Observer<S> { state ->
            state.apply {
                data?.let {
                    renderData(it)
                }
            }
        })
    }

    abstract fun renderData (data: T)
}