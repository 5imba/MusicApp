package com.bogleo.musicapp.common.extensions

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.core.net.toUri
import com.bogleo.musicapp.data.model.Song
import java.lang.Exception

fun Song.toMediaMetadata(): MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, uri.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artworkUri.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, artworkUri.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        .putString(MediaMetadataCompat.METADATA_KEY_DATE, dateAdded)
        .build()

fun MediaMetadataCompat.toSong(): Song = Song(
        uri = (getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI) ?: "").toUri(),
        artworkUri = (getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI) ?: "").toUri(),
        id = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) ?: "",
        title = getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
        artist = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE) ?: "",
        album = getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
        duration = getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
        dateAdded = getString(MediaMetadataCompat.METADATA_KEY_DATE) ?: ""
)