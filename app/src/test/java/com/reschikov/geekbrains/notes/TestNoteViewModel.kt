package com.reschikov.geekbrains.notes

import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class TestNoteViewModel {

    @ExperimentalCoroutinesApi
    private  val testDispatcher =  TestCoroutineDispatcher()

    private val mockRepository = mockk<Repository>()
    private val spyRouterSupportMessage = mockk<RouterSupportMessage>()

    private val testId = "Id"

    private val noteViewModel = NoteViewModel(mockRepository, spyRouterSupportMessage)
    private val mockModule = module {
        single { spyRouterSupportMessage }
    }

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
//        every { mockRepository.getNoteById(any()) } returns notesLiveData
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `successfully load Note`(){
        val testId = "id"
        var result: Any? = null

        MainScope().launch {
            result = noteViewModel.loadNote(testId)
        }

        assertNotNull(result)
        assertEquals(Unit, result)
    }

    @Test
    fun `failed to load Note`(){
        startKoin {modules(mockModule)}
        val testError = Throwable("error")
        var result: String? = null
        val slot = slot<String>()

        every { spyRouterSupportMessage.sendMessage(capture(slot)) } answers  {
            result = slot.captured }

        noteViewModel.loadNote(testId)

        assertEquals(testError.message, result)
        stopKoin()
    }

    @Test
    fun `unauthorized to load Note`(){
        startKoin {  }
        val testError = NoAuthException()
        var result: Throwable? = null
        val spyNoteResultError = spyk(NoteResult.Error(testError))
        val slot = slot<NoAuthException>()

//        every { spyNoteResultError.renderError(capture(slot)) } answers {
//            result = slot.captured }

//        noteViewModel.loadNote(testId)
//        notesLiveData.value = spyNoteResultError
//
//        assertEquals(testError, result)
//        stopKoin()
    }

    @Test
    fun `successfully delete note`(){
        val testNote = Note(id = "id")
        var result: Note? = testNote
//        val spyNoteViewModel = spyk(noteViewModel, recordPrivateCalls = true)
//
//        every { mockRepository.deleteNote(any()) } returns notesLiveData
//        every {spyNoteViewModel["close"]() } answers { Unit.also { result = null }  }
//
//        spyNoteViewModel.saveChanges(testNote)
//        spyNoteViewModel.delete()
//
//        notesLiveData.value = null
//
//        assertNull(result)
//
//        verify(exactly = 1) {  mockRepository.deleteNote(any()) }
    }

    @ExperimentalCoroutinesApi
    @After
    fun toFinis(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines ()
    }
}