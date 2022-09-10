package com.bogleo.musicapp.adaptors.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.bogleo.musicapp.R
import com.bogleo.musicapp.adaptors.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.adaptors.recycler.managers.SongDiffUtil
import com.bogleo.musicapp.adaptors.recycler.viewholders.SongViewHolder
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.databinding.ItemSongBinding
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SongListAdapter @Inject constructor() : ListAdapter<Song, BaseViewHolder<ViewBinding, Song>>(
    SongDiffUtil()
) {

    private var onClickListener: ((Song) -> Unit)? = null

    fun setOnClickListener(listener: (Song) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding, Song> {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            R.layout.item_song -> {
                val binding = ItemSongBinding.inflate(inflater, parent, false)
                SongViewHolder(
                    binding = binding,
                    onClickListener = onClickListener
                )
            }
            else -> throw IllegalArgumentException("View type not found: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding, Song>, position: Int) {
        holder.onBind(item = currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_song
    }
}

