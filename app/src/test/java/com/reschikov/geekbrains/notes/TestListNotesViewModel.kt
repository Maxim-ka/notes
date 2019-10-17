package com.reschikov.geekbrains.notes

import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
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
    private val mockChannel = mockk<Channel<NoteResult>>()
    @ExperimentalCoroutinesApi
    private lateinit var listNotesViewModel: ListNotesViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp () {
        Dispatchers.setMain(testDispatcher)
        every { mockRepository.getNotes() } answers {mockChannel}
        listNotesViewModel = spyk(ListNotesViewModel(mockRepository), recordPrivateCalls = true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return error`() {
        val testError = Throwable("error")
        var result: Throwable? = null

        every { mockChannel.offer(NoteResult.Error(testError)) } returns true
        every { listNotesViewModel getProperty "channelNoteResult" } propertyType ReceiveChannel::class answers {mockChannel}

        MainScope().launch {
            result = listNotesViewModel.getErrorChannel().receive()
        }

        assertNotNull(result)
        assertEquals(Throwable("error"), result)
        assertEquals(testError, result)
        assertEquals(testError.message, result?.message)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))

//        scopeTest.launch {
//            listNotesViewModel.getViewState().consumeEach {
//                result = it
//            }
//            assertNotNull(testData)
//            assertNotNull(result)
//            assertEquals(testData, result)
//            assertEquals(arrayOf(listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))), result)
//        }
//        scopeTest.advanceUntilIdle()
    }

    @ExperimentalCoroutinesApi
    @After
    fun toFinis(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines ()
    }
}