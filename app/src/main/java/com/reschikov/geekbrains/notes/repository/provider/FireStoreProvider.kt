package com.reschikov.geekbrains.notes.repository.provider

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.reschikov.geekbrains.notes.NoteApp
import com.reschikov.geekbrains.notes.repository.NoAuthException
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult
import com.reschikov.geekbrains.notes.repository.model.User
import java.util.*

private const val USERS_COLLECTION = "users"
private const val NOTES_COLLECTION = "notes"
private const val USER = "user"
private const val ANONYMOUS = "anonymous"

class FireStoreProvider : RemoteDataProvider {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser

    private val anonymous: String by lazy {
        toAssign()
    }

    private fun toAssign(): String{
        val context = NoteApp.INSTANCE.applicationContext
        val preferences = context.getSharedPreferences(USER, Context.MODE_PRIVATE)
        var name = preferences.getString(ANONYMOUS, null)
        if (name == null){
            name = UUID.randomUUID().toString()
            preferences.edit().putString(ANONYMOUS, name).apply()
        }
        return name
    }

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection()
                    .addSnapshotListener { snapshot, e ->
                        e?.let { throw it } ?: snapshot?.let { it ->
                            value = NoteResult.Success(it.map { it.toObject(Note::class.java) })
                        }
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(id)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        value = NoteResult.Success(snapshot.toObject(Note::class.java))
                    }
                    .addOnFailureListener { throw it }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }
    }

    override fun addNewNotes(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection()
                    .add(note)
                    .addOnSuccessListener {
                        value = NoteResult.Success(note)
                    }
                    .addOnFailureListener {
                        OnFailureListener {
                            throw it
                        }
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }
    }

    override fun saveChangesNote(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            note.id?.let { it ->
                try {
                    getUserNotesCollection().document(it)
                        .set(note)
                        .addOnSuccessListener {
                            value = NoteResult.Success(note)
                        }
                        .addOnFailureListener {
                            OnFailureListener {
                                throw it
                            }
                        }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }
        }
    }

    override fun deleteNote(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            note.id?.let { it ->
                try {
                    getUserNotesCollection().document(it)
                            .delete()
                            .addOnSuccessListener {
                                value = subscribeToAllNotes().value
                            }
                            .addOnFailureListener {
                                OnFailureListener {
                                    throw it
                                }
                            }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }
        }
    }

    private fun getUserNotesCollection() = currentUser?.let {
        val docPath = if (it.isAnonymous) anonymous else it.uid
        db.collection(USERS_COLLECTION).document(docPath).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override fun getCurrentUser(): LiveData<User?> =
        MutableLiveData<User?>().apply {
            value = currentUser?.let{ User(it.displayName ?: "",
                    it.email ?: "") }
        }
}