package com.reschikov.geekbrains.notes.view.fragments.adapters

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.getDateTime
import com.reschikov.geekbrains.notes.getResourceColor
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.view.activities.OnItemClickListener
import kotlinx.android.synthetic.main.item_note.view.*
import java.util.*

private const val HALF = 0.5f

class ListNotesAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ListNotesAdapter.ViewHolder>() {

    var notes = mutableListOf<Note>()
        set(value){
            //FixMe разобраться почему при пустом notes не запускается NotesDiffUtilCallback
            if (notes.isEmpty()){
                notifyDataSetChanged()
            } else {
                NotesDiffUtilCallback(notes, value).also {
                    DiffUtil.calculateDiff(it).dispatchUpdatesTo(this)
                }
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        val metricsB = DisplayMetrics()
        parent.display.getMetrics(metricsB)
        view.layoutParams.height = (metricsB.widthPixels / metricsB.density * HALF).toInt()
        return ViewHolder(view, onItemClickListener)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.show(notes[position])
    }

    class ViewHolder(private val view : View, private val onItemClickListener: OnItemClickListener): RecyclerView.ViewHolder(view),
            DisplayedNote{

        override fun show(note: Note) = view.run{
            setOnClickListener {onItemClickListener.onItemClick(note.id!!)}
            with(note){
                tv_title.text = title
                tv_text.text = this.note
                tv_time.text = getDateTime(Date(lastModification))
                cv_card.setBackgroundColor(ContextCompat.getColor(view.context, getResourceColor(color)))
            }
        }
    }
}