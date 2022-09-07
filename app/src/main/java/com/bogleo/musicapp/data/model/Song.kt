package com.bogleo.musicapp.data.model

import android.graphics.Bitmap
import android.net.Uri

data class Song(
    val uri: Uri,
    val artworkUri: Uri,
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val dateAdded: String
)