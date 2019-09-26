package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.reschikov.geekbrains.notes.viewmodel.fragments.BaseViewModel
import com.reschikov.geekbrains.notes.usecase.BaseViewState
import com.reschikov.geekbrains.notes.view.activities.DisplayedMessage

abstract class BaseFragment<T, S : BaseViewState<T>>(layoutId: Int) : Fragment(layoutId) {

    abstract val viewModel: BaseViewModel<T, S>
    lateinit var displayedMessage: DisplayedMessage

    override fun onAttach(context: Context) {
        super.onAttach(context)
        displayedMessage = context as DisplayedMessage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getViewState().observe( this, Observer<S> { state ->
            state?.let {
                it.data?.apply {
                    renderData(this)
                    return@Observer
                }
                it.error?.apply {displayedMessage.showMessage(this.message.toString()) }
            }
        })
    }

    abstract fun renderData (data: T)
}