package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.getResourceColor
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.usecase.NoteViewState
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import kotlinx.android.synthetic.main.note_fragment.*
import java.util.*

private const val KEY_ID = "key id"
private const val SAVE_DELAY = 2_000L

class EditorNoteFragment: BaseFragment<Note?, NoteViewState>(R.layout.note_fragment) {

    companion object{
        fun newInstance(id: String) = EditorNoteFragment()
            .also {
                with(Bundle()){
                    putString(KEY_ID, id)
                    it.arguments = this
                }
            }
    }

    override val viewModel: NoteViewModel by lazy {
        ViewModelProviders.of(this).get(NoteViewModel::class.java)
    }

    private var note: Note? = null
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(KEY_ID)?.let { viewModel.loadNote(it) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setTitleNote()
        setTextNote()
    }

    private fun setTitleNote(){
        tie_title.requestFocus()
        tie_title.addTextChangedListener(object : AdapterTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    note = note?.copy(title = tie_title?.text.toString(), lastModification = Date().time) ?:
                            Note(title = tie_title?.text.toString())
                    viewModel.saveChanges(note!!)
                }, SAVE_DELAY)
            }
        })
    }

    private fun setTextNote(){
        tie_text.addTextChangedListener(object : AdapterTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    note = note?.copy(note = tie_text?.text.toString(), lastModification = Date().time) ?:
                            Note(note = tie_text?.text.toString())
                    viewModel.saveChanges(note!!)
                }, SAVE_DELAY)
            }
        })
    }

    override fun renderData(data: Note?) {
        note = data
        initView()
    }

    private fun initView(){
        note?.let {
            with(til_title){
                boxBackgroundColor = ContextCompat.getColor(context, getResourceColor(it.color))
            }
            with(tie_title){
                editableText.append(it.title)
            }
            with(til_text){
                boxBackgroundColor = ContextCompat.getColor(context, getResourceColor(it.color))
            }
            with(tie_text){
                editableText.append(it.note)
                requestFocus()
            }
        }
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