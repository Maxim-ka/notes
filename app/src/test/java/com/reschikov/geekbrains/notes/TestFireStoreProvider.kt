package com.reschikov.geekbrains.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.repository.preference.Storage
import com.reschikov.geekbrains.notes.repository.provider.FireStoreProvider
import io.mockk.*
import org.junit.*
import org.junit.Assert.*

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.ArgumentMatchers.any
import timber.log.Timber
import java.lang.RuntimeException
import com.google.firebase.firestore.FirebaseFirestoreException as FirebaseFirestoreException

class TestFireStoreProvider{

    @get:Rule
    val taskExecutorRule =  InstantTaskExecutorRule()

    private val mockStorage = mockk<Storage>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockBase = mockk<FirebaseFirestore>()
    private val mockCurrentUser = mockk<FirebaseUser>()
    private val mockUserCollection = mockk<CollectionReference>()
    private val mockUserDocument = mockk<DocumentReference>()
    private val mockResultCollection = mockk<CollectionReference>()

    private val mockDocument_1 = mockk<DocumentSnapshot>()
    private val mockDocument_2 = mockk<DocumentSnapshot>()
    private val mockDocument_3 = mockk<DocumentSnapshot>()

    private val testNotes = listOf(
            Note(id = "1"),
            Note(id = "2"),
            Note(id = "3"))

    private val provider = FireStoreProvider(mockStorage, mockAuth, mockBase)


    companion object{
        @BeforeClass  @JvmStatic
        fun toPrepare(){
            startKoin { }
        }

        @AfterClass @JvmStatic
        fun toFinish(){
            stopKoin()
        }
    }

    @Before
    fun setup(){
        every { mockAuth.currentUser} returns mockCurrentUser
        every { mockCurrentUser.isAnonymous } returns false
        every { mockCurrentUser.uid } returns ""
        every { mockBase.collection(any()) } returns mockUserCollection
        every { mockUserCollection.document(any())} returns mockUserDocument
        every { mockUserDocument.collection(any())} returns mockResultCollection

        every { mockDocument_1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument_2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument_3.toObject(Note::class.java) } returns testNotes[2]


    }

    @Test
    fun `should throw NoAuthException if no authorization`(){
        var result: Any? = null
        every { mockAuth.currentUser } returns null
        provider.subscribeToAllNotes().observeForever{
            result = (it as? NoteResult.Error)?.error

        }
        assertTrue(result is NoAuthException)
    }

    @Test
    fun `subscribeToAllNotes return notes`(){
        var result: Any? = null
        val mockQuerySnapshot = mockk<QuerySnapshot>()
        val slot =  slot<EventListener<QuerySnapshot>>()
        every {mockQuerySnapshot.documents } returns listOf(mockDocument_1, mockDocument_2, mockDocument_3)
        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()
        provider.subscribeToAllNotes().observeForever{
            result = (it as? NoteResult.Success<List<Note>>)?.data
        }
        slot.captured.onEvent(mockQuerySnapshot, null)
        assertEquals(testNotes, result)
    }

    //FIXME не прошел
    @Test
    fun `subscribeToAllNotes return error`(){
        var result: Throwable? = null
//        val spyError = spyk(FirebaseFirestoreException("Provided message must not be null", FirebaseFirestoreException.Code.ABORTED))
        val testError = mockk<FirebaseFirestoreException>()
        val slot = slot<EventListener<QuerySnapshot>>()
        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()

        provider.subscribeToAllNotes().observeForever{
            result = (it as NoteResult.Error).error
        }
        slot.captured.onEvent(null, testError)
        assertEquals(testError, result)
    }

    @Test
    fun `successfully get note by id`(){
        val slot = slot<OnSuccessListener<DocumentSnapshot>>()
        var result: Note? = null
        every {
            mockResultCollection
                .document(testNotes[0].id!!)
                .get()
                .addOnSuccessListener(capture(slot))} returns mockk()
        provider.getNoteById(testNotes[0].id!!).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }
        slot.captured.onSuccess(mockDocument_1)

        assertNotNull(result)
        assertEquals(testNotes[0], result)

        verify(exactly = 1) { mockResultCollection
                .document(testNotes[0].id!!)
                .get() }

    }

    @Test
    fun `successfully add a new note`(){
        val slot = slot<OnSuccessListener<DocumentReference>>()
        var result: Note? = null
        every {
            mockResultCollection
                .add(testNotes[1])
                .addOnSuccessListener(capture(slot))} returns mockk()
        provider.addNewNotes(testNotes[1]).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }
        slot.captured.onSuccess(mockUserDocument)

        assertNotNull(result)
        assertEquals(testNotes[1], result)

        verify(exactly = 1) { mockResultCollection
                .add(testNotes[1])}

    }

    @Test
    fun `successfully save note changes`(){
        val slot = slot<OnSuccessListener<Void>>()
        var result: Note? = null
        every {
            mockResultCollection
                .document(testNotes[0].id!!)
                .set(testNotes[0])
                .addOnSuccessListener(capture(slot))} returns mockk()
        provider.saveChangesNote(testNotes[0]).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }
        slot.captured.onSuccess(null)

        assertNotNull(result)
        assertEquals(testNotes[0], result)

        verify(exactly = 1) {  mockResultCollection
                .document(testNotes[0].id!!)
                .set(testNotes[0]) }
    }

    @Test
    fun `successfully delete note`(){
        val slot = slot<OnSuccessListener<Void>>()
        var result: Note? = null
        every {
            mockResultCollection
                .document(testNotes[0].id!!)
                .delete()
                .addOnSuccessListener(capture(slot))} returns mockk()
        provider.deleteNote(testNotes[0].id!!).observeForever {
            result = (it as NoteResult.Success<Note>).data
        }
        slot.captured.onSuccess(null)

        assertNull(result)
        assertEquals(null, result)

        verify(exactly = 1) {  mockResultCollection
                .document(testNotes[0].id!!)
                .delete() }
    }

    //FixMe так и не проходит никак
    @Test
    fun `unsuccessfully delete note`(){
        val slot = slot<OnFailureListener>()
        val mockException = mockk<FirebaseFirestoreException>(relaxed = true)
        var result: Throwable? = null
        every {
            mockResultCollection
                .document(any())
                .delete()
                .addOnFailureListener(capture(slot))} returns mockk()

        provider.deleteNote(testNotes[0].id!!).observeForever {
            result = (it as NoteResult.Error).error
        }
        slot.captured.onFailure(mockException)

        assertNotNull(result)
        assertEquals(mockException, result)

//        verify(exactly = 1) {  mockResultCollection
//                .document(any())
//                .delete() }
    }

    @After
    fun toFinis(){
        clearAllMocks()
        unmockkAll()
    }
}