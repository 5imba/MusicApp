package com.bogleo.musicapp.data

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.bogleo.musicapp.R
import com.bogleo.musicapp.common.utils.sdk29AndUp
import com.bogleo.musicapp.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class MediaDatabase(
    private val context: Context
) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val songList: MutableList<Song> = mutableListOf()
    private var isPermissionGranted = false

    suspend fun loadMusic(): List<Song> {
        return withContext(Dispatchers.IO) {

            while (!isPermissionGranted) {
                checkPermission()
                delay(100)
                yield()
            }

            val audioLibraryUri = sdk29AndUp {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            contentResolver.query(
                audioLibraryUri,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                songList.clear()
                while (cursor.moveToNext()) {
                    val id = cursor.getString(idCol)
                    val title = cursor.getString(titleCol)
                    val artist = cursor.getString(artistCol)
                    val album = cursor.getString(albumCol)
                    val albumId = cursor.getString(albumIdCol)
                    val duration = cursor.getLong(durationCol)
                    val dateAdded = cursor.getString(dateAddedCol)

                    val songUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id.toLong()
                    )
                    val retriever = MediaMetadataRetriever()
                        .apply {
                            setDataSource(context, songUri)
                        }
                    val artwork = retriever.embeddedPicture


                    val artworkUri =
                        if(artwork != null) {
                            ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId.toLong()
                            )
                        } else {
                            Uri.parse("android.resource://"
                                + context.packageName + "/"
                                + R.drawable.ic_default_album_cover)
                        }

                    val song = Song(songUri, artworkUri, id, title,
                        artist, album, duration, dateAdded)

                    songList.add(song)
                }
                songList.toList()
            } ?: listOf()
        }
    }

    private fun checkPermission() {
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        this.isPermissionGranted = true
    }
}