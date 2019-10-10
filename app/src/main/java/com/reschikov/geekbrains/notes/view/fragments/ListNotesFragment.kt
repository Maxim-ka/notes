package com.reschikov.geekbrains.notes.view.fragments

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.usecase.ListNoteViewState
import com.reschikov.geekbrains.notes.view.fragments.adapters.ListNotesAdapter
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import kotlinx.android.synthetic.main.list_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val SPAN_COUNT = 2

class ListNotesFragment : BaseFragment<MutableList<Note>?, ListNoteViewState>(R.layout.list_fragment), OnItemClickListener{

    override val model: ListNotesViewModel by viewModel()

    private val notesAdapter: ListNotesAdapter by lazy {
        ListNotesAdapter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler_notes.apply {
            adapter = notesAdapter
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            setHasFixedSize(true)
        }
    }

    override fun renderData (data: MutableList<Note>?) {
        data?.let{
                notesAdapter.notes = it
        }
    }

    override fun onItemClick(note: Note) {
        model.deleteNote(note)
    }
}