package com.bogleo.musicapp.common

import android.content.Context
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager

class ScreenUtils {

    companion object {

        fun getScreenWidth(context: Context): Int {
            val windowManager = context.getSystemService(WindowManager::class.java)

            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val wm = windowManager.currentWindowMetrics
                Size(wm.bounds.width(), wm.bounds.height()).width
            } else {
                val dm = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(dm)
                dm.widthPixels
            }
        }
    }
}