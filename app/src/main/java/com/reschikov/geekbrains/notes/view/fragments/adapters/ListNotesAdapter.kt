package com.reschikov.geekbrains.notes.view.fragments.adapters

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reschikov.geekbrains.notes.*
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.view.fragments.OnItemClickListener
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.view.navigation.Screens
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.properties.Delegates

private const val HALF = 0.5f

class ListNotesAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ListNotesAdapter.ViewHolder>() {

    var notes: MutableList<Note>  by Delegates.observable(mutableListOf()){
        _, oldValue, newValue ->
        run {
            if (oldValue.isEmpty()) {
                notifyDataSetChanged()
            }else{
                val diffUtilCallback = NotesDiffUtilCallback(oldValue, newValue)
                DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(this)
            }
        }
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

    class ViewHolder(override val containerView: View,
                     private val onItemClickListener: OnItemClickListener):
            RecyclerView.ViewHolder(containerView),
            DisplayedNote,
            LayoutContainer,
            KoinComponent{

        private val router: RouterSupportMessage by inject()

        override fun show(note: Note) {
            itemView.setOnClickListener {router.replaceScreen(Screens.NoteScreen(note.id))}
            itemView.setOnLongClickListener{onItemClickListener.onItemClick(note).let { true }}
            with(note){
                tv_title.text = title
                tv_text.text = this.note
                tv_time.text = lastModification.formatDateTime()
                cv_card.setBackgroundColor(color.getColor(itemView.context))
            }
        }
    }
}