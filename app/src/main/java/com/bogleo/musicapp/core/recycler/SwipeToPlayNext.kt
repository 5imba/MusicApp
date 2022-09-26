package com.bogleo.musicapp.core.recycler

import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bogleo.musicapp.R
import com.bogleo.musicapp.core.recycler.managers.BaseViewHolder
import com.bogleo.musicapp.data.model.Song


class SwipeToPlayNext(
    val onSwipe: (Song) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.LEFT
) {

    private var _swipeDrawData: SwipeDrawData? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Call listeners
        when(direction) {
            ItemTouchHelper.LEFT -> {
                val baseViewHolder = viewHolder as BaseViewHolder<*, *>
                onSwipe(baseViewHolder.item as Song)
            }
        }
        // Reset data
        _swipeDrawData = null
        viewHolder.bindingAdapter?.notifyItemChanged(
            viewHolder.bindingAdapterPosition
        )
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (viewHolder.itemViewType != R.layout.item_song) {
            ItemTouchHelper.ACTION_STATE_IDLE
        } else {
            super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = 0.3f

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView: View = viewHolder.itemView
            val swipeDrawData = getSwipeDrawData(itemView, dX)

            // Draw background
            c.drawRect(
                itemView.right.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat() + dX,
                itemView.bottom.toFloat(),
                swipeDrawData.backgroundPaint
            )
            //Draw icon
            val iconMarginRight = (dX * -0.1f).coerceAtMost(70f).coerceAtLeast(0f)
            swipeDrawData.icon?.let {
                c.drawBitmap(
                    it,
                    itemView.right.toFloat() - iconMarginRight - it.width,
                    itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - it.height) / 2,
                    swipeDrawData.iconPaint
                )
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun getSwipeDrawData(itemView: View, dX: Float): SwipeDrawData {
        // Get new instance if dX is 0 (start position) or is null
        val swipeDrawData: SwipeDrawData =
            if (dX == 0f) SwipeDrawData(itemView)
            else _swipeDrawData ?: SwipeDrawData(itemView)

        _swipeDrawData = swipeDrawData
        return swipeDrawData
    }

    inner class SwipeDrawData(itemView: View) {

        val iconPaint: Paint
        val backgroundPaint: Paint
        val icon: Bitmap?

        init {
            val context = itemView.context

            // Get icon bitmap
            val iconSize = itemView.height / 3
            icon = ContextCompat.getDrawable(context, R.drawable.ic_music_queue_add)
                ?.toBitmap(iconSize, iconSize)

            // Get colored Paints from theme attributes
            val theme = context.theme
            val typedValue = TypedValue()
            // Icon paint
            theme.resolveAttribute(
                R.attr.colorHighlighted,
                typedValue,
                true
            )
            iconPaint = Paint().also {
                it.colorFilter = PorterDuffColorFilter(
                    typedValue.data,
                    PorterDuff.Mode.SRC_IN
                )
            }
            // Background paint
            theme.resolveAttribute(
                R.attr.colorBackground,
                typedValue,
                true
            )
            backgroundPaint = Paint().also {
                it.color = typedValue.data
            }
        }
    }
}