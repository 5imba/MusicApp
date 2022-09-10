package com.bogleo.musicapp.common.extensions

import android.view.View
import com.google.android.material.math.MathUtils.lerp

fun View.lerpWidth(widthStart: Int, widthStop: Int, amount: Float) {
    lerpWidth(widthStart.toFloat(), widthStop.toFloat(), amount)
}

fun View.lerpWidth(widthStart: Float, widthStop: Float, amount: Float) {
    val params = layoutParams
    params.width = lerp(widthStart, widthStop, amount).toInt()
    layoutParams = params
}

fun View.lerpHeight(startHeight: Int, stopHeight: Int, amount: Float) {
    lerpHeight(startHeight.toFloat(), stopHeight.toFloat(), amount)
}

fun View.lerpHeight(startHeight: Float, stopHeight: Float, amount: Float) {
    val params = layoutParams
    params.height = lerp(startHeight, stopHeight, amount).toInt()
    layoutParams = params
}

fun View.lerpX(startX: Float, stopX: Float, amount: Float) {
    x = lerp(startX, stopX, amount)
}

fun View.lerpY(startY: Float, stopY: Float, amount: Float) {
    y = lerp(startY, stopY, amount)
}