package com.bogleo.musicapp.core.recycler.viewholders

import com.bogleo.musicapp.core.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.databinding.ItemTabBinding

class TabSliderViewHolder(
    binding: ItemTabBinding,
    private val onClickListener: ((String) -> Unit)? = null
) : BaseViewHolder<ItemTabBinding, String>(binding = binding) {

    override fun onBind(item: String) {
        super.onBind(item)
        with(binding) {
            txtTabTitle.text = item
            root.setOnClickListener {
                onClickListener?.let { it(item) }
            }
        }
    }
}