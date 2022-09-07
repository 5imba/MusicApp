package com.bogleo.musicapp.screens.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bogleo.musicapp.common.EMPTY_ROOT
import com.bogleo.musicapp.common.Resource
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.player.MusicServiceConnection
import com.bogleo.musicapp.player.isPlayEnabled
import com.bogleo.musicapp.player.isPlaying
import com.bogleo.musicapp.player.isPrepared
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val permissionError = musicServiceConnection.permissionError
    val currentlyPlayingSong = musicServiceConnection.currentlyPlayingSong
    val playbackState = musicServiceConnection.playbackState

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }

    fun playOrToggleSong(
        mediaItem: Song,
        toggle: Boolean = false
    ) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem.id ==
            currentlyPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> {
                        if(toggle) {
                            musicServiceConnection.transportControls.pause()
                        }
                    }
                    playbackState.isPlayEnabled -> {
                        musicServiceConnection.transportControls.play()
                    }
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(
                mediaItem.id.toString(),
                null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            EMPTY_ROOT,
            subscriptionCallback
        )
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)

            Log.e("onChildrenLoaded", "Start")

            // TODO fix that later
            val items = children.map {
                val extras = it.description.extras
                Song(
                    uri = it.description.mediaUri!!,
                    artworkUri = it.description.iconUri!!,
                    id = it.mediaId!!,
                    title = it.description.title.toString(),
                    artist = extras?.getString(METADATA_KEY_ARTIST)
                        ?: it.description.description.toString(),
                    album = extras?.getString(METADATA_KEY_ALBUM) ?: "",
                    duration = extras?.getLong(METADATA_KEY_DURATION) ?: 0L,
                    dateAdded = extras?.getString(METADATA_KEY_DATE) ?: ""
                )
            }
            _mediaItems.postValue(
                Resource.success(data = items)
            )
            Log.e("onChildrenLoaded", "End")
        }
    }

    init {
        _mediaItems.postValue(Resource.loading(data = null))
        musicServiceConnection.subscribe(
            EMPTY_ROOT,
            subscriptionCallback
        )
    }
}