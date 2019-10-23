package com.reschikov.geekbrains.notes.view.fragments

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.view.fragments.adapters.ListNotesAdapter
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import kotlinx.android.synthetic.main.list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val SPAN_COUNT = 2

class ListNotesFragment : BaseFragment<List<Note>?>(R.layout.list_fragment), OnItemClickListener{

    @ExperimentalCoroutinesApi
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

    override fun renderData(data: List<Note>?) {
        data?.let {
            notesAdapter.notes = it
        }
    }

    @ExperimentalCoroutinesApi
    override fun onItemClick(note: Note) {
        model.deleteNote(note)
    }
}