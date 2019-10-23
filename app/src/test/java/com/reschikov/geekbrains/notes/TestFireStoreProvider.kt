package com.reschikov.geekbrains.notes

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.repository.preference.Storage
import com.reschikov.geekbrains.notes.repository.provider.FireStoreProvider
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import com.google.firebase.firestore.FirebaseFirestoreException

class TestFireStoreProvider {

    @ExperimentalCoroutinesApi
    private  val testDispatcher =  TestCoroutineDispatcher()

    private val mockStorage = mockk<Storage>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockBase = mockk<FirebaseFirestore>()
    private val mockCurrentUser = mockk<FirebaseUser>()
    private val mockUserCollection = mockk<CollectionReference>()
    private val mockUserDocument = mockk<DocumentReference>()
    private val mockResultCollection = mockk<CollectionReference>()

    private val mockDocument1 = mockk<DocumentSnapshot>()
    private val mockDocument2 = mockk<DocumentSnapshot>()
    private val mockDocument3 = mockk<DocumentSnapshot>()

    private lateinit var slotSuccessVoid: CapturingSlot<OnSuccessListener<Void>>
    private lateinit var slotError: CapturingSlot<OnFailureListener>

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

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        every { mockAuth.currentUser} returns mockCurrentUser
        every { mockCurrentUser.isAnonymous } returns false
        every { mockCurrentUser.uid } returns ""
        every { mockBase.collection(any()) } returns mockUserCollection
        every { mockUserCollection.document(any())} returns mockUserDocument
        every { mockUserDocument.collection(any())} returns mockResultCollection

        every { mockDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument3.toObject(Note::class.java) } returns testNotes[2]

       slotSuccessVoid = slot()
       slotError = slot()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should throw NoAuthException if no authorization`(){
        var result: Any? = null
        every { mockAuth.currentUser } returns null

        MainScope().launch {
                provider.subscribeToAllNotes().consumeEach {
                    if(it is NoteResult.Error){
                        result = it.error
                    }
                }
            }
        assertTrue(result is NoAuthException)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `subscribeToAllNotes return notes`(){
        var result: Any? = null
        val mockQuerySnapshot = mockk<QuerySnapshot>()
        val slot =  slot<EventListener<QuerySnapshot>>()

        every {mockQuerySnapshot.documents } returns listOf(mockDocument1, mockDocument2, mockDocument3)
        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()

        MainScope().launch {
            provider.subscribeToAllNotes().consumeEach {
                result = (it as NoteResult.Success).data
            }
        }

        slot.captured.onEvent(mockQuerySnapshot, null)

        assertEquals(testNotes, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `subscribeToAllNotes return error`(){
        var result: Throwable? = null
        val testError = mockk<FirebaseFirestoreException>()
        val slot = slot<EventListener<QuerySnapshot>>()
        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()

        MainScope().launch {
            provider.subscribeToAllNotes().consumeEach {
                result = (it as NoteResult.Error).error
            }
        }

        slot.captured.onEvent(null, testError)
        assertEquals(testError, result)
    }

    @Test
    fun `successfully get note by id`(){
        val slot = slot<OnSuccessListener<DocumentSnapshot>>()
        var result: Note? = null
        val id = "1"

        every {
            mockResultCollection
                .document(any())
                .get()
                .addOnSuccessListener(capture(slot))
                .addOnFailureListener(capture(slotError))} returns mockk()

        MainScope().launch {
           result = provider.getNoteById(id)
        }

        slot.captured.onSuccess(mockDocument1)
        slotError.clear()

        assertNotNull(result)
        assertEquals(testNotes[0], result)

        verify(exactly = 1) { mockResultCollection
                .document(any())
                .get() }
    }

    @Test
    fun `successfully add a new note`(){
        val slot = slot<OnSuccessListener<DocumentReference>>()
        var result: Any? = null

        every {
            mockResultCollection
                .add(any())
                .addOnSuccessListener(capture(slot))
                .addOnFailureListener(capture(slotError))} returns mockk()

        MainScope().launch {
            result = provider.addNewNotes(testNotes[1])
        }

        slot.captured.onSuccess(mockUserDocument)
        slotError.clear()

        assertNotNull(result)
        assertEquals(Unit, result)

        verify(exactly = 1) { mockResultCollection.add(any())}
    }

    @Test
    fun `successfully save note changes`(){
        var result: Any? = null

        every {
            mockResultCollection
                .document(any())
                .set(any())
                .addOnSuccessListener(capture(slotSuccessVoid))
                .addOnFailureListener(capture(slotError))} returns mockk()

        MainScope().launch {
            result = provider.saveChangesNote(testNotes[0])
        }

        slotSuccessVoid.captured.onSuccess(null)
        slotError.captured

        assertNotNull(result)
        assertEquals(Unit, result)

        verify(exactly = 1) {  mockResultCollection
                .document(any())
                .set(any()) }
    }

    @Test
    fun `successfully delete note`(){
        var result: Any? = null

        every {
            mockResultCollection
                .document(any())
                .delete()
                .addOnSuccessListener(capture(slotSuccessVoid))
                .addOnFailureListener(capture(slotError))} returns mockk()

        MainScope().launch {
            result = provider.deleteNote(testNotes[0].id!!)
        }

        slotSuccessVoid.captured.onSuccess(null)
        slotError.captured

        assertNotNull(result)
        assertEquals(Unit, result)

        verify(exactly = 1) {  mockResultCollection
                .document(any())
                .delete() }
    }

    @Test
    fun `unsuccessfully delete note`(){
        val mockException = mockk<FirebaseFirestoreException>(relaxed = true)
        var result: Throwable? = null

        every {
            mockResultCollection
                .document(any())
                .delete()
                .addOnSuccessListener(capture(slotSuccessVoid))
                .addOnFailureListener(capture(slotError))} returns mockk()

        MainScope().launch {
            try {
                provider.deleteNote(testNotes[0].id!!)
            } catch (e: Throwable){
                result = e
            }
        }

        slotSuccessVoid.captured
        slotError.captured.onFailure(mockException)

        assertNotNull(result)
        assertEquals(mockException, result)

        verify(exactly = 1) {  mockResultCollection
                .document(any())
                .delete() }
    }

    @ExperimentalCoroutinesApi
    @After
    fun toFinis(){
        clearAllMocks()
        unmockkAll()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines ()
    }
}