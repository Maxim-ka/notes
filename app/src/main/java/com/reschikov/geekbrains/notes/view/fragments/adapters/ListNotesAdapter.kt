package com.reschikov.geekbrains.notes.view.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.repository.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class ListNotesAdapter : RecyclerView.Adapter<ListNotesAdapter.ViewHolder>() {

    var notes = mutableListOf<Note>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.show(notes[position])
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