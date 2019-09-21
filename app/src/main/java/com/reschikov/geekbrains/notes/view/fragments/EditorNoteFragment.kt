package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.getResourceColor
import com.reschikov.geekbrains.notes.repository.model.ColorNote
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import kotlinx.android.synthetic.main.note_fragment.*
import java.util.*

private const val NOTE = "note"
private const val SAVE_DELAY = 2_000L

class EditorNoteFragment: Fragment(R.layout.note_fragment) {

    companion object{

        fun newInstance(note: Note) = EditorNoteFragment()
            .apply {
                val bundle = Bundle()
                bundle.putParcelable(NOTE, note)
                arguments = bundle
            }
    }

    private var note: Note? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var imm: InputMethodManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        noteViewModel = ViewModelProviders.of( this).get(NoteViewModel::class.java)
        note = arguments?.get(NOTE) as Note?
        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setTitleNote()
        setTextNote()
    }

    private fun setTitleNote(){
        if (note != null){
            til_title.boxBackgroundColor = ContextCompat.getColor(tie_title.context, getResourceColor(note!!.color))
            if (note!!.title != null) tie_title.editableText.append(note!!.title)
        } else {
            tie_title.requestFocus()
        }
        tie_title.addTextChangedListener(object : AdapterTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    note = note?.copy(title = tie_title?.text.toString(), lastModification = Date().time) ?:
                            Note(title = tie_title?.text.toString(), note = null, color = ColorNote.WHITE)
                    noteViewModel.saveChanges(note!!)
                }, SAVE_DELAY)
            }
        })
    }

    private fun setTextNote(){
        if (note != null){
            til_text.boxBackgroundColor = ContextCompat.getColor(tie_text.context, getResourceColor(note!!.color))
            if (note!!.note != null) tie_text.editableText.append(note!!.note)
            tie_text.requestFocus()
        }
        tie_text.addTextChangedListener(object : AdapterTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    note = note?.copy(note = tie_text?.text.toString(), lastModification = Date().time) ?:
                            Note(title = null, note = tie_text?.text.toString(), color = ColorNote.WHITE)
                    noteViewModel.saveChanges(note!!)
                }, SAVE_DELAY)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (tie_title.isFocused || tie_text.isFocused) {
            val view: View = if (tie_text.isCursorVisible) tie_text else tie_title
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }

    override fun onStop() {
        super.onStop()
        val view: View = if (tie_text.isCursorVisible) tie_text else tie_title
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}