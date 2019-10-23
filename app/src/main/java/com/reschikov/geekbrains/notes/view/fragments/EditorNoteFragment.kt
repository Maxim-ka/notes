package com.reschikov.geekbrains.notes.view.fragments

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.getColor
import com.reschikov.geekbrains.notes.repository.model.ColorNote
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import kotlinx.android.synthetic.main.note_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.anko.alert
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

private const val KEY_ID = "key id"
private const val SAVE_DELAY = 2_000L

class EditorNoteFragment : BaseFragment<Note?>(R.layout.note_fragment) {

    companion object{
        fun newInstance(id: String?) = EditorNoteFragment()
            .also {
                with(Bundle()){
                    putString(KEY_ID, id)
                    it.arguments = this
                }
            }
    }

    override val model: NoteViewModel by viewModel()

    private var note: Note? = null
    private val imm: InputMethodManager by currentScope.inject()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(KEY_ID)?.let {
            model.loadNote(it) } ?: expectative.toFinish()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        setTitleNote()
        setTextNote()
        colorPicker.onColorClickListener = {
            setColorNote(it)
        }
    }

    private fun setTitleNote(){
        tie_title.requestFocus()
        tie_title.addTextChangedListener(object : AdapterTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    note = note?.copy(title = tie_title?.text.toString(), lastModification = Date().time) ?:
                            Note(title = tie_title?.text.toString())
                    note?.let { model.saveChanges(it) }
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
                    note?.let { model.saveChanges(it) }
                }, SAVE_DELAY)
            }
        })
    }

    private fun setColorNote(color: ColorNote){
        toPaintFields(color)
        Handler().post {
            note = note?.copy(color = color, lastModification = Date().time) ?:
                    Note(color = color)
            note?.let { model.saveChanges(it) }
        }
    }

    private fun toPaintFields(color: ColorNote){
        tie_title.apply { setBackgroundColor(color.getColor(context)) }
        tie_text.apply { setBackgroundColor(color.getColor(context)) }
    }

    override fun renderData(data: Note?) {
        note = data
        initView()
    }

    private fun initView(){
        note?.let {
            with(til_title){
                boxBackgroundColor = it.color.getColor(this.context)
            }
            with(tie_title){
                it.title?.let{
                    with(editableText){clear()
                        append(it)
                    }
                }
            }
            with(til_text){
                boxBackgroundColor = it.color.getColor(this.context)
            }
            with(tie_text){
                it.note?.let{
                    with(editableText){
                        clear()
                        append(it)
                    }
                }
                requestFocus()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater.inflate(R.menu.menu_editor_note_fragment, menu)
    }

    override fun onOptionsItemSelected (item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.palette -> togglePalette().let { true }
                R.id.delete_forever -> deleteNote().let { true }
                else -> false
            }

    private fun togglePalette(){
        if (colorPicker.isOpen) {
            colorPicker.close()
        } else {
            colorPicker.open()
        }
    }

    private fun deleteNote(){
        context?.alert {
            messageResource = R.string.delete_note
            positiveButton(R.string.dialog_ok) {
                model.delete()
                it.dismiss()
            }
        }?.show()
    }

    @ExperimentalCoroutinesApi
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