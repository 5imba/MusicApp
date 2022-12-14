package com.bogleo.musicapp.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.bogleo.musicapp.common.extensions.toMediaMetadata
import com.bogleo.musicapp.data.MediaDatabase
import com.bogleo.musicapp.data.model.Song
import javax.inject.Inject
import com.bogleo.musicapp.player.MusicSourceState.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class MusicSourceState {
    object CreatedState : MusicSourceState()
    object InitializingState : MusicSourceState()
    object InitializedState : MusicSourceState()
    object ErrorState : MusicSourceState()
}

class MusicSource @Inject constructor(
    private val mediaDatabase: MediaDatabase
) {

    var songs = emptyList<MediaMetadataCompat>()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: MusicSourceState = CreatedState
    set(value) {
        if (value == InitializingState || value == InitializedState) {
            synchronized(onReadyListeners) {
                field = value
                onReadyListeners.forEach { listener ->
                    listener(state == InitializedState)
                }
            }
        } else {
            field = value
        }
    }

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = InitializingState
        val songList = mediaDatabase.loadMusic()
        songs = songList.map { song: Song ->
            song.toMediaMetadata()
        }
        state = InitializedState
    }

    fun asMediaSource(dataSourceFactory: DataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song: MediaMetadataCompat ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.fromUri(
                        song.getString(METADATA_KEY_MEDIA_URI)
                    )
                )
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song: MediaMetadataCompat ->
        val descriptionCompat = MediaDescriptionCompat.Builder()
            .setMediaUri(song.description.mediaUri)
            .setIconUri(song.description.iconUri)
            .setMediaId(song.description.mediaId)
            .setTitle(song.getString(METADATA_KEY_TITLE))
            .setSubtitle(song.getString(METADATA_KEY_ARTIST))
            .setDescription(song.getString(METADATA_KEY_ARTIST))
            .setExtras(bundleOf(
                pairFromMetadata(METADATA_KEY_ARTIST, song),
                pairFromMetadata(METADATA_KEY_DISPLAY_DESCRIPTION, song),
                pairFromMetadata(METADATA_KEY_ALBUM, song),
                pairFromMetadata(METADATA_KEY_DURATION, song),
                pairFromMetadata(METADATA_KEY_DATE, song)
            ))
            .build()
        MediaBrowserCompat.MediaItem(descriptionCompat, FLAG_PLAYABLE)
    }.toMutableList()

    fun whenReady(performAction: (Boolean) -> Unit): Boolean =
        when (state) {
            CreatedState, InitializingState -> {
                onReadyListeners += performAction
                false
            }
            else -> {
                performAction(state == InitializedState)
                true
            }
        }

    private fun pairFromMetadata(key: String, value: MediaMetadataCompat) = Pair(
        key, value.getString(key)
    )
}