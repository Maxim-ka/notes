package com.reschikov.geekbrains.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import io.mockk.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone

class TestListNotesViewModel {

    @get:Rule
    val taskExecutorRule =  InstantTaskExecutorRule()

    private val mockRepository = mockk<Repository>()
    private val notesLiveData = MutableLiveData<NoteResult>()
    private val spyRouter = spyk(RouterSupportMessage(Cicerone.create().router))
    private val mockNavigator = module {
        single { spyRouter }
    }

    private lateinit var listNotesViewModel: ListNotesViewModel

    @Before
    fun setUp () {
        every { mockRepository.getNotes() } returns notesLiveData
        listNotesViewModel = ListNotesViewModel(mockRepository)
    }


    @Test
    fun `should return error`() {
        startKoin { modules(mockNavigator) }
        val testError = Throwable("error")
        var result: String? = null
        val slot = slot<String>()
        every { spyRouter.sendMessage(capture(slot)) } answers  {result = slot.captured}

        notesLiveData.value = NoteResult.Error(testError)

        assertEquals(testError.message, result)
        stopKoin()

        verify(exactly = 1 ) { mockRepository.getNotes() }
    }

    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))
        listNotesViewModel.getViewState().observeForever { result = it?. data }
        notesLiveData.value = NoteResult.Success(testData)

        assertEquals(testData, result)

        verify(exactly = 1 ) { mockRepository.getNotes() }
    }
}