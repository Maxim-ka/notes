package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.repository.model.Note
import com.reschikov.geekbrains.notes.usecase.ListNoteViewState
import com.reschikov.geekbrains.notes.view.fragments.adapters.ListNotesAdapter
import com.reschikov.geekbrains.notes.view.activities.OnItemClickListener
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import kotlinx.android.synthetic.main.list_fragment.*

private const val SPAN_COUNT = 2

class ListNotesFragment : BaseFragment<MutableList<Note>?, ListNoteViewState>(R.layout.list_fragment){

    override val viewModel: ListNotesViewModel by lazy {
        ViewModelProviders.of(this).get(ListNotesViewModel::class.java)
    }

    private lateinit var onItemClickListener: OnItemClickListener
    private val  notesAdapter : ListNotesAdapter by lazy {
        ListNotesAdapter(onItemClickListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onItemClickListener = context as OnItemClickListener
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
}