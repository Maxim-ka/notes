package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.viewmodel.fragments.BaseViewModel
import com.reschikov.geekbrains.notes.view.activities.Expectative
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<T>(layoutId: Int) : Fragment(layoutId), CoroutineScope {

    private val router: RouterSupportMessage by inject()

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    abstract val model: BaseViewModel<T>

    protected lateinit var expectative: Expectative

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job

    override fun onAttach(context: Context) {
        super.onAttach(context)
        expectative = context as Expectative
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expectative.toBegin()
    }

    @ExperimentalCoroutinesApi
    override fun onStart () {
        super .onStart()
        dataJob = launch {
            model.getViewState().consumeEach {
                renderData(it)
                expectative.toFinish()
            }
        }
        errorJob = launch {
            model.getErrorChannel().consumeEach {
                renderError(it)
            }
        }
    }

    abstract fun renderData (data: T?)

    private fun renderError (error: Throwable) {
        when (error) {
            is NoAuthException -> router.replaceScreen(Screens.AuthScreen())
            else -> error.message?.let {
                router.sendMessage(it)
            }
        }
    }

    override fun onStop () {
        super .onStop()
        dataJob.cancel()
        errorJob.cancel()
    }

    override fun onDestroy () {
        super .onDestroy()
        coroutineContext.cancel()
    }
}