package com.reschikov.geekbrains.notes.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.usecase.StateListNote
import com.reschikov.geekbrains.notes.view.fragments.adapters.ListNotesAdapter
import com.reschikov.geekbrains.notes.view.activities.OnItemClickListener
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import kotlinx.android.synthetic.main.list_fragment.*

class ListNotesFragment : Fragment(R.layout.list_fragment){

    companion object{
        private const val SPAN_COUNT = 2
    }

    private lateinit var  onItemClickListener: OnItemClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onItemClickListener = context as OnItemClickListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerView = recycler_notes
        val adapter = ListNotesAdapter(onItemClickListener)
        val viewModel = ViewModelProviders.of(this).get(ListNotesViewModel::class.java)
        viewModel.getViewStateListNote().observe(this, Observer<StateListNote> {state ->
            state.let{
                adapter.notes = it.list
            }})
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        recyclerView.setHasFixedSize(true)
    }
}