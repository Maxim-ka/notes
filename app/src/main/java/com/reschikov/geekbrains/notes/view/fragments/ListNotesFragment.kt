package com.reschikov.geekbrains.notes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.usecase.StateListNote
import com.reschikov.geekbrains.notes.view.fragments.adapters.ListNotesAdapter
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import kotlinx.android.synthetic.main.list_fragment.view.*

class ListNotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.list_fragment, container, false)
        val recyclerView: RecyclerView = view.recycler_notes
        val viewModel: ListNotesViewModel = ViewModelProviders.of(this).get(ListNotesViewModel::class.java)
        viewModel.getViewStateListNote().observe(this, Observer<StateListNote> {stateListNote ->
            stateListNote.apply{
            recyclerView.adapter?.notifyDataSetChanged()
        }})
        recyclerView.adapter = ListNotesAdapter(viewModel)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)
        return view
    }
}