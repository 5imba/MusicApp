package com.bogleo.musicapp.adaptors.recycler.viewholders

import com.bogleo.musicapp.adaptors.recycler.other.BaseViewHolder
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.databinding.ItemSongBinding

class SongViewHolder(
    binding: ItemSongBinding,
    private val onClickListener: ((Song) -> Unit)? = null
) : BaseViewHolder<ItemSongBinding, Song>(binding = binding) {

    override fun onBind(item: Song) {
        super.onBind(item)
        with(binding) {

            imgAlbumCover.setImageURI(item.artworkUri)
            txtTitle.text = item.title
            txtArtist.text = item.artist

            root.setOnClickListener {
                onClickListener?.let { listener -> listener(item) }
            }
        }
    }
}