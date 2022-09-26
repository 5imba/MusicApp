package com.bogleo.musicapp.common.extensions

import android.view.View
import android.view.ViewGroup
import com.bogleo.musicapp.common.LerpAnimator

fun View.lerpAnimate(amount: Float) = LerpAnimator(view = this, amount = amount)

fun View.setMargins(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val lp = layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(
        left ?: lp.leftMargin,
        top ?: lp.topMargin,
        right ?: lp.rightMargin,
        bottom ?: lp.bottomMargin
    )
    layoutParams = lp
}