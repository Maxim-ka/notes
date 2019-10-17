package com.reschikov.geekbrains.notes.repository.provider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.repository.model.User
import com.reschikov.geekbrains.notes.repository.preference.Storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val USERS_COLLECTION = "users"
private const val NOTES_COLLECTION = "notes"

class FireStoreProvider(private val storage: Storage,
                        private val firebaseAuth: FirebaseAuth,
                        private val db : FirebaseFirestore) : RemoteDataProvider {

    private val currentUser
        get() = firebaseAuth.currentUser

    private val anonymous: String by lazy {
        storage.toAssign()
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToAllNotes():  ReceiveChannel<NoteResult> {
        return Channel<NoteResult>(Channel.CONFLATED).apply{
            var registration: ListenerRegistration? = null
            try {
                registration = getUserNotesCollection()
                    .addSnapshotListener { snapshot, e ->
                        val value = e?.let { NoteResult.Error(it) } ?:
                        snapshot?.let { it ->
                            NoteResult.Success(it.documents.map { it.toObject(Note::class.java)!!})
                        }
                        value?.let { offer(it) }
                    }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }
            invokeOnClose { registration?.remove() }
        }
    }

    override suspend fun getNoteById(id: String): Note {
        return suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(id)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot.toObject(Note::class.java)!!)
                    }
                    .addOnFailureListener {continuation.resumeWithException(it) }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun addNewNotes(note: Note){
        return suspendCoroutine{ continuation ->
            try {
                getUserNotesCollection()
                    .add(note)
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun saveChangesNote(note: Note) {
        return suspendCoroutine{continuation ->
            note.id?.let { it ->
                try {
                    getUserNotesCollection().document(it)
                        .set(note)
                        .addOnSuccessListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener {
                            continuation.resumeWithException(it)
                        }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun removeItem(id: String): ReceiveChannel<NoteResult> {
        return suspendCoroutine{continuation ->
            try {
                getUserNotesCollection().document(id)
                        .delete()
                        .addOnSuccessListener {
                            continuation.resume(subscribeToAllNotes())
                        }
                        .addOnFailureListener {
                            continuation.resumeWithException(it)
                        }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun deleteNote(id: String){
        return suspendCoroutine{continuation ->
            try {
                getUserNotesCollection().document(id)
                        .delete()
                        .addOnSuccessListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener {
                            continuation.resumeWithException(it)
                        }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun getUserNotesCollection() = currentUser?.let {
        val docPath = if (it.isAnonymous) anonymous else it.uid
        db.collection(USERS_COLLECTION).document(docPath).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override suspend fun getCurrentUser(): User? =
        suspendCoroutine{continuation ->
            continuation.resume(currentUser?.let{ User(it.displayName ?: "",
                it.email ?: "") })
        }
}