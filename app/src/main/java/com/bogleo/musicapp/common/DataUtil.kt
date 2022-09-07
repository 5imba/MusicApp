package com.bogleo.musicapp.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}

@Suppress("DEPRECATION")
fun Context.getBitmap(uri: Uri?): Bitmap? =
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri!!))
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    } catch (e: Exception) {
        null
    }
