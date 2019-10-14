package com.reschikov.geekbrains.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class TestNoteViewModel {

    @get:Rule
    val taskExecutorRule =  InstantTaskExecutorRule()

    private val mockRepository = mockk<Repository>()
    private val spyRouterSupportMessage = mockk<RouterSupportMessage>()
    private val notesLiveData = MutableLiveData<NoteResult>()
    private val testId = "Id"

    private val noteViewModel = NoteViewModel(mockRepository, spyRouterSupportMessage)
    private val mockModule = module {
        single { spyRouterSupportMessage }
    }

    @Before
    fun setup(){
        every { mockRepository.getNoteById(any()) } returns notesLiveData
    }

    @Test
    fun `successfully load Note`(){
        val testNote = Note(id = "1", title = "AAAAAA", note = "aaaaaa")
        var result: Note? = null

        noteViewModel.loadNote(testNote.id!!)
        noteViewModel.getViewState().observeForever{
           result = it.data
        }
        notesLiveData.value = NoteResult.Success(testNote)

        assertEquals(testNote, result)
        assertEquals(testNote.id, result?.id)
        assertEquals(testNote.title, result?.title)
        assertEquals(testNote.note, result?.note)
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
        notesLiveData.value = NoteResult.Error(testError)

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

        every { spyNoteResultError.renderError(capture(slot)) } answers {
            result = slot.captured }

        noteViewModel.loadNote(testId)
        notesLiveData.value = spyNoteResultError

        assertEquals(testError, result)
        stopKoin()
    }

    @Test
    fun `successfully delete note`(){
        val testNote = Note(id = "id")
        var result: Note? = testNote
        val spyNoteViewModel = spyk(noteViewModel, recordPrivateCalls = true)

        every { mockRepository.deleteNote(any()) } returns notesLiveData
        every {spyNoteViewModel["close"]() } answers { Unit.also { result = null }  }

        spyNoteViewModel.saveChanges(testNote)
        spyNoteViewModel.delete()

        notesLiveData.value = null

        assertNull(result)

        verify(exactly = 1) {  mockRepository.deleteNote(any()) }
    }
}