package com.bogleo.musicapp.core.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.bogleo.musicapp.core.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.data.model.Item
import java.lang.IllegalArgumentException

class ListItemAdapter(
    private val listItems: List<ListItem<*, *>>
) : ListAdapter<Item, BaseViewHolder<ViewBinding, Item>>(
    ListItemDiffUtil(listItems = listItems)
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding, Item> {
        val inflater = LayoutInflater.from(parent.context)
        return listItems.find { it.getLayoutId() == viewType }
            ?.getViewHolder(layoutInflater = inflater, parent = parent)
            ?.let { it as BaseViewHolder<ViewBinding, Item> }
            ?: throw IllegalArgumentException("View type not found: $viewType")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding, Item>, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        return listItems.find { it.isRelativeItem(item = item) }
            ?.getLayoutId()
            ?: throw IllegalArgumentException("View type not found: $item")
    }
}