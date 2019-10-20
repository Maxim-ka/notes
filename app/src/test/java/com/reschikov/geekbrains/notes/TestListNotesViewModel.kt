package com.reschikov.geekbrains.notes

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class TestListNotesViewModel {

    @ExperimentalCoroutinesApi
    private  val testDispatcher =  TestCoroutineDispatcher()
    private val mockRepository = mockk<Repository>()
    @ExperimentalCoroutinesApi
    private lateinit var listNotesViewModel: ListNotesViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp () {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return error`() {
        val testError = Throwable("error")
        var result: Throwable? = null

        every { mockRepository.getNotes() } returns  MainScope().produceChannel(NoteResult.Error(testError))
        listNotesViewModel = ListNotesViewModel(mockRepository)

        mockkObject(listNotesViewModel)

        MainScope().launch {
            result = listNotesViewModel.getErrorChannel().receive()
        }

        assertNotNull(result)
        assertEquals(testError, result)
        assertEquals(testError.message, result?.message)
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.produceChannel(result: NoteResult): ReceiveChannel<NoteResult> = produce {
        send(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))

        every { mockRepository.getNotes() } returns  MainScope().produceChannel(NoteResult.Success(testData))
        listNotesViewModel = ListNotesViewModel(mockRepository)

        mockkObject(listNotesViewModel)

        MainScope().launch {
            result = listNotesViewModel.getViewState().receive()
        }

        assertNotNull(testData)
        assertNotNull(result)
        assertEquals(testData, result)
    }

    @ExperimentalCoroutinesApi
    @After
    fun toFinis(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines ()
    }
}