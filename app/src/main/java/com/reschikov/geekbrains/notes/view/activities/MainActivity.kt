package com.reschikov.geekbrains.notes.view.activities

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.view.fragments.EditorNoteFragment
import com.reschikov.geekbrains.notes.view.fragments.ListNotesFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG_LIST_NOTES = "tag list notes"
private const val TAG_NOTE = "tag note"

class MainActivity : AppCompatActivity(), OnItemClickListener, DisplayedMessage {

    private lateinit var plus : Drawable
    private lateinit var done : Drawable
    private lateinit var fab : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        initFab()

        savedInstanceState ?: loadFragment(ListNotesFragment(), TAG_LIST_NOTES)
    }

    private fun initFab(){
        plus = ContextCompat.getDrawable(baseContext, R.drawable.ic_add_24dp)!!
        done = ContextCompat.getDrawable(baseContext, R.drawable.ic_done_white_24dp)!!
        fab = fb_fab
        fab.setOnClickListener {onClick()}
        setImageFabForTag(getTag())
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_master, fragment, tag)
            .commit()
        setImageFabForTag(tag)
    }

    private fun setImageFabForTag(tag: String?) {
        if (tag.equals(TAG_LIST_NOTES)){
            fab.setImageDrawable(plus)
        } else {
            fab.setImageDrawable(done)
        }
    }

    private fun onClick() {
        when (getTag() ?: return) {
            TAG_LIST_NOTES -> {
                loadFragment(EditorNoteFragment(), TAG_NOTE)
            }
            TAG_NOTE -> {
                loadFragment(ListNotesFragment(), TAG_LIST_NOTES)
            }
        }
    }

    private fun getTag(): String? {
        return if (supportFragmentManager.fragments.size == 0) null
               else supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1].tag
    }

    override fun onItemClick(noteId: String) {
        loadFragment(EditorNoteFragment.newInstance(noteId), TAG_NOTE)
    }

    override fun onBackPressed() {
        if (getTag().equals(TAG_NOTE)){
            loadFragment(ListNotesFragment(), TAG_LIST_NOTES)
            return
        }
        super.onBackPressed()
    }

    override fun showMessage(message: String){
        val snackbar = Snackbar.make(bottomAppBar, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.anchorView = fab
        snackbar.show()
    }
}
