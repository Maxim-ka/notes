package com.reschikov.geekbrains.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import io.mockk.*
import org.junit.Assert.assertEquals
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
    private val mockRouterSupportMessage = mockk<RouterSupportMessage>()
    private val notesLiveData = MutableLiveData<NoteResult>()

    private val noteViewModel = NoteViewModel(mockRepository, mockRouterSupportMessage)
    private val mockModule = module {
        single { mockRouterSupportMessage }
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
        every { mockRouterSupportMessage.sendMessage(capture(slot)) } answers  {result = slot.captured}
        noteViewModel.loadNote("1")
        notesLiveData.value = NoteResult.Error(testError)

        assertEquals(testError.message, result)
        stopKoin()
    }

    @Test
    fun `unauthorized to load Note`(){
        startKoin { modules(mockModule) }
        val testError = NoAuthException()
        var result: Throwable? = null
        val mockScreen = mockk<Screens.AuthScreen>(relaxed = true)
        every { listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build()) } returns listOf()
        every {mockRouterSupportMessage.replaceScreen(mockScreen) } answers {
            result = NoAuthException()}
        noteViewModel.loadNote("1")
        notesLiveData.value = NoteResult.Error(testError)

        assertEquals(testError, result)
        stopKoin()
    }
}