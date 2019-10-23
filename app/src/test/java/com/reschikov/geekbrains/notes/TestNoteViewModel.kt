package com.reschikov.geekbrains.notes

import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TestNoteViewModel {

    @ExperimentalCoroutinesApi
    private  val testDispatcher =  TestCoroutineDispatcher()

    private val mockRepository = mockk<Repository>()
    private val spyRouterSupportMessage = mockk<RouterSupportMessage>()
    private val testId = "id"
    private val noteViewModel = NoteViewModel(mockRepository, spyRouterSupportMessage)

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        mockkObject(noteViewModel)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `successfully load Note`(){
        val testNote = Note(id = testId)
        var result: Note? = null

        coEvery {
                mockRepository.getNoteById(any())
        } returns testNote

        noteViewModel.loadNote(testId)

        MainScope().launch {
            result = noteViewModel.getViewState().receive()
        }

        assertNotNull(result)
        assertEquals(testNote, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `failed to load Note`(){
        val testError = Throwable("error")
        var result: Throwable? = null

        coEvery {
            mockRepository.getNoteById(any())
        } throws (testError)

        noteViewModel.loadNote(testId)

        MainScope().launch {
            result = noteViewModel.getErrorChannel().receive()
        }

        assertNotNull(result)
        assertEquals(testError, result)
        assertEquals(testError.message, result?.message)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `unauthorized to load Note`(){
        val testError = NoAuthException()
        var result: Throwable? = null

        coEvery {
            mockRepository.getNoteById(any())
        } throws (testError)

        noteViewModel.loadNote(testId)

        MainScope().launch {
            result = noteViewModel.getErrorChannel().receive()
        }

        assertNotNull(result)
        assertEquals(testError, result)
    }

    @Test
    fun `successfully delete note`(){
        val testNote = Note(id = testId)
        var result: Note? = testNote

        val spyNoteViewModel = spyk(noteViewModel, recordPrivateCalls = true)

        coEvery { mockRepository.deleteNote(any()) } returns Unit
        every {spyNoteViewModel["close"]() } answers { Unit.also { result = null }  }

        spyNoteViewModel.saveChanges(testNote)
        spyNoteViewModel.delete()

        assertNull(result)

        coVerify(exactly = 1) {  mockRepository.deleteNote(any()) }
    }

    @ExperimentalCoroutinesApi
    @After
    fun toFinis(){
        unmockkAll()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines ()
    }
}