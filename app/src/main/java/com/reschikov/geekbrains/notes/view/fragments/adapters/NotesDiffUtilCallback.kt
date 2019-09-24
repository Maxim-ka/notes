package com.reschikov.geekbrains.notes.view.fragments.adapters

import androidx.recyclerview.widget.DiffUtil
import com.reschikov.geekbrains.notes.repository.model.Note

class NotesDiffUtilCallback(private val oldList: MutableList<Note>, private val newList: MutableList<Note>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title.equals(newList[newItemPosition].title) &&
               oldList[oldItemPosition].note.equals(newList[newItemPosition].note) &&
               oldList[oldItemPosition].color == newList[newItemPosition].color &&
               oldList[oldItemPosition].lastModification == newList[newItemPosition].lastModification
    }
}