package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.reschikov.geekbrains.notes.viewmodel.fragments.BaseViewModel
import com.reschikov.geekbrains.notes.usecase.BaseViewState
import com.reschikov.geekbrains.notes.view.activities.Expectative

abstract class BaseFragment<T, S : BaseViewState<T>>(layoutId: Int) : Fragment(layoutId) {

    abstract val model: BaseViewModel<T, S>

    protected lateinit var expectative: Expectative

    override fun onAttach(context: Context) {
        super.onAttach(context)
        expectative = context as Expectative
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expectative.toBegin()
        model.getViewState().observe(this, Observer<S> { state ->
            state.apply {
                data?.let {
                    renderData(it)
                }
                expectative.toFinish()
            }
        })
    }

    abstract fun renderData (data: T)
}