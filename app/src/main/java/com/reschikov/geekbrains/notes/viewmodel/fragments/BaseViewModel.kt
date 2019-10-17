package com.reschikov.geekbrains.notes.viewmodel.fragments

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel <T> : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.IO + Job()
    }

    @ExperimentalCoroutinesApi
    private val viewStateChannel = BroadcastChannel<T>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    @ExperimentalCoroutinesApi
    fun getViewState (): ReceiveChannel<T> = viewStateChannel.openSubscription()
    fun getErrorChannel (): ReceiveChannel<Throwable> = errorChannel

    protected fun setError (e: Throwable) {
        launch {
            errorChannel.send(e)
        }
    }

    @ExperimentalCoroutinesApi
    protected fun setData (data: T) {
        launch {
            viewStateChannel.send(data)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCleared () {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super .onCleared()
    }
}