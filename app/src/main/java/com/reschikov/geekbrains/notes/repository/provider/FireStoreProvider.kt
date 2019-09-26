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
        val result = MutableLiveData<NoteResult>()
        notesReference.addSnapshotListener {snapshot, e ->
            e?.let { result.value = NoteResult.Error(it) } ?:
            snapshot?.let {
                val notes = mutableListOf<Note>()
                for (doc: QueryDocumentSnapshot in it) {
                    notes.add(doc.toObject(Note::class.java))
                }
                result.value = NoteResult.Success(notes)
            }
        }
        return result
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReference.document(id)
            .get()
            .addOnSuccessListener {snapshot ->
                result.value = NoteResult.Success(snapshot.toObject(Note::class.java ))
            }
            .addOnFailureListener {result.value = NoteResult.Error(it) }
        return result
    }

    override fun addNewNotes(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReference
                .add(note)
                .addOnSuccessListener {
                    result.value = NoteResult.Success(note)}
                .addOnFailureListener {
                    OnFailureListener {
                        error -> result.value = NoteResult.Error(error)
                    }
                }
        return result
    }

    override fun saveChangesNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        note.id?.let {notesReference.document(it)
            .set(note)
            .addOnSuccessListener {
                result.value = NoteResult.Success(note)}
            .addOnFailureListener {
                OnFailureListener {
                    error -> result.value = NoteResult.Error(error)
                }
            }
        }
        return result
    }
}