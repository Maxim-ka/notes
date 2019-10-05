package com.reschikov.geekbrains.notes.view.fragments

import com.reschikov.geekbrains.notes.repository.model.Note

interface OnItemClickListener {
    fun onItemClick (note: Note)
}