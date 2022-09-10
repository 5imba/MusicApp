package com.bogleo.musicapp.adaptors.recycler.managers

import androidx.recyclerview.widget.DiffUtil
import com.bogleo.musicapp.data.model.Song

class SongDiffUtil : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}