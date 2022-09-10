package com.bogleo.musicapp.adaptors.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bogleo.musicapp.R
import com.bogleo.musicapp.adaptors.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.adaptors.recycler.viewholders.TabSliderViewHolder
import com.bogleo.musicapp.databinding.ItemTabBinding
import java.lang.IllegalArgumentException
import javax.inject.Inject

class TabSliderAdapter @Inject constructor()
    : RecyclerView.Adapter<BaseViewHolder<ViewBinding, String>>() {
    
    private val tabs: MutableList<String> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<String>) {
        tabs.clear()
        tabs.addAll(data)
        notifyDataSetChanged()
    }

    private var onClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(listener: (Int) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding, String> {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            R.layout.item_tab -> {
                val binding = ItemTabBinding.inflate(inflater, parent, false)
                TabSliderViewHolder(
                    binding = binding,
                    onClickListener = ::onClickCallback
                )
            }
            else -> throw IllegalArgumentException("View type not found: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding, String>, position: Int) {
        holder.onBind(item = tabs[position])
    }

    override fun getItemCount(): Int = tabs.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_tab
    }

    private fun onClickCallback(item: String) {
        onClickListener?.let { it(tabs.indexOf(item)) }
    }
}