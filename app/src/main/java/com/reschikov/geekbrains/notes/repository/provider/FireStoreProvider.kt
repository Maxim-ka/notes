package com.reschikov.geekbrains.notes.repository.provider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.*
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.repository.model.NoteResult

private const val NOTES_COLLECTION = "notes"

class FireStoreProvider : RemoteDataProvider {

    private val db = FirebaseFirestore.getInstance()
    private val notesReference = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReference
                .addSnapshotListener {snapshot, e ->
                e?.let { value = NoteResult.Error(it) } ?:
                snapshot?.let {
                    mutableListOf<Note>().apply {
                        for (doc: QueryDocumentSnapshot in it) {
                            add(doc.toObject(Note::class.java))
                        }
                        value = NoteResult.Success(this)
                    }
                }
            }
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReference.document(id)
                .get()
                .addOnSuccessListener {snapshot ->
                    value = NoteResult.Success(snapshot.toObject(Note::class.java ))
                }
                .addOnFailureListener {value = NoteResult.Error(it) }
        }
    }

    override fun addNewNotes(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReference
                .add(note)
                .addOnSuccessListener {
                    value = NoteResult.Success(note)
                }
                .addOnFailureListener {
                    OnFailureListener {
                        value = NoteResult.Error(it)
                    }
                }
        }
    }

    override fun saveChangesNote(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            note.id?.let {
                notesReference.document(it)
                    .set(note)
                    .addOnSuccessListener {
                        value = NoteResult.Success(note)
                    }
                    .addOnFailureListener {
                        OnFailureListener {
                            error -> value = NoteResult.Error(error)
                        }
                    }
            }
        }
    }
}