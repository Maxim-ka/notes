package com.reschikov.geekbrains.notes.view.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.usecase.DisplayedNotes
import kotlinx.android.synthetic.main.item_note.view.*

class ListNotesAdapter (private val displayedNotes : DisplayedNotes): RecyclerView.Adapter<ListNotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = displayedNotes.getNumberOfNotes()

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        displayedNotes.bindNoteWithView(holder, position)
    }

    class ViewHolder(private val view : View): RecyclerView.ViewHolder(view), DisplayedNote{

        override fun show(note : Note) = view.run{
            with(note){
                tv_title.text = title
                tv_text.text = this.note
                cv_card.setBackgroundColor(color)
            }
        }
    }
}