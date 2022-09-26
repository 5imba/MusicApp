package com.bogleo.musicapp.core.recycler.listitems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bogleo.musicapp.R
import com.bogleo.musicapp.core.recycler.ListItem
import com.bogleo.musicapp.core.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.data.model.Item
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.databinding.ItemSongBinding

class SongListItem(
    private val onClickListener: (Song) -> Unit,
    private val onLongClickListener: (Song) -> Unit
) : ListItem<ItemSongBinding, Song> {

    override fun isRelativeItem(item: Item) = item is Song

    override fun getLayoutId(): Int = R.layout.item_song

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemSongBinding, Song> {
        val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
        return SongViewHolder(
            binding = binding,
            onClickListener = onClickListener,
            onLongClickListener = onLongClickListener
        )
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
    }
}

class SongViewHolder(
    binding: ItemSongBinding,
    private val onClickListener: ((Song) -> Unit)? = null,
    private val onLongClickListener: ((Song) -> Unit)? = null
) : BaseViewHolder<ItemSongBinding, Song>(binding = binding) {

    override fun onBind(item: Song) {
        super.onBind(item)
        with(binding) {

            imgAlbumCover.setImageURI(item.artworkUri)
            txtTitle.text = item.title
            txtArtist.text = item.artist

            root.apply {
                setOnClickListener {
                    onClickListener?.let { listener -> listener(item) }
                }
                setOnLongClickListener {
                    onLongClickListener
                        ?.let { listener ->
                            listener(item)
                            true
                        }
                        ?: false
                }
            }
        }
    }
}