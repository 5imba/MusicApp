package com.bogleo.musicapp.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bogleo.musicapp.common.Event
import com.bogleo.musicapp.common.PERMISSION_ERROR
import com.bogleo.musicapp.common.Resource

class MusicServiceConnection(
    context: Context
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _permissionError = MutableLiveData<Event<Resource<Boolean>>>()
    val permissionError: LiveData<Event<Resource<Boolean>>> = _permissionError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentlyPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentlyPlayingSong: LiveData<MediaMetadataCompat?> = _currentlyPlayingSong

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        Log.e("MusicServiceConnection", "connect")
        connect()
    }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        val mediaControllerCallback = MediaControllerCallback()

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(mediaControllerCallback)
            }
            _isConnected.postValue(Event(Resource.success(data = true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error(
                message = "Connection suspended",
                data = false
            )))
            mediaController.unregisterCallback(mediaControllerCallback)
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error(
                message = "Couldn't connect to media browser",
                data = false
            )))
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event) {
                PERMISSION_ERROR -> _permissionError.postValue(
                    Event(
                        Resource.error(
                            message = "Allow to read data",
                            data = null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}