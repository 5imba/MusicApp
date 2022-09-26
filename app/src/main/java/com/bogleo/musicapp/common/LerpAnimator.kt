package com.bogleo.musicapp.common

import android.view.View
import com.google.android.material.math.MathUtils

class LerpAnimator(
    private val view: View,
    private val amount: Float
) {
    private val layoutParams = view.layoutParams
    private var x = view.x
    private var y = view.y

    fun lerpWidth(start: Int, stop: Int): LerpAnimator {
        return lerpWidth(start.toFloat(), stop.toFloat())
    }

    fun lerpWidth(start: Float, stop: Float): LerpAnimator {
        layoutParams.width = MathUtils.lerp(start, stop, amount).toInt()
        return this
    }

    fun lerpHeight(start: Int, stop: Int): LerpAnimator {
        return lerpHeight(start.toFloat(), stop.toFloat())
    }

    fun lerpHeight(start: Float, stop: Float): LerpAnimator {
        layoutParams.height = MathUtils.lerp(start, stop, amount).toInt()
        return this
    }

    fun lerpX(start: Float, stop: Float): LerpAnimator {
        x = MathUtils.lerp(start, stop, amount)
        return this
    }

    fun lerpY(start: Float, stop: Float): LerpAnimator {
        y = MathUtils.lerp(start, stop, amount)
        return this
    }

    fun apply() {
        view.apply {
            this.x = this@LerpAnimator.x
            this.y = this@LerpAnimator.y
            this.layoutParams = this@LerpAnimator.layoutParams
        }
    }
}