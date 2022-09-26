package com.bogleo.musicapp.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.bogleo.musicapp.common.EMPTY_ROOT
import com.bogleo.musicapp.common.PERMISSION_ERROR
import com.bogleo.musicapp.player.callbacks.MusicPlaybackPreparer
import com.bogleo.musicapp.player.callbacks.MusicPlayerEventListener
import com.bogleo.musicapp.player.callbacks.MusicPlayerNotificationListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DataSource.Factory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private lateinit var musicNotificationManager: MediaNotificationManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    private var currentPlaylistItems: MutableList<MediaMetadataCompat> = mutableListOf()
    private var currentMediaItemIndex: Int = 0

    private var isPlayerInitialized = false
    var isForegroundService = false

    companion object {
        var currentSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()

        Log.e("onCreate", "MediaBrowserService")
        serviceScope.launch {
            musicSource.fetchMediaData()
        }

        // Build a PendingIntent that can be used to launch the UI
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        // Create a new MediaSession
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource = musicSource) {
            if (it != null) {
                currentPlaylistItems.add(it)
            }
            preparePlayer(
                songs = musicSource.songs,
                songToPlay = it,
                playNow = true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
            .apply {
                setPlaybackPreparer(musicPlaybackPreparer)
                setPlayer(exoPlayer)
                setQueueNavigator(MusicQueueNavigator())
            }

        musicPlayerEventListener = MusicPlayerEventListener(musicService = this)
        exoPlayer.addListener(musicPlayerEventListener)

        musicNotificationManager = MediaNotificationManager(
            context = this,
            sessionToken = mediaSession.sessionToken,
            notificationListener = MusicPlayerNotificationListener(this)
        ) {
            currentSongDuration = exoPlayer.duration
        }
        musicNotificationManager.showNotification(exoPlayer)
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        songToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val currentSongIndex = if (songToPlay == null) 0 else songs.indexOf(songToPlay)

        serviceScope.launch {
            exoPlayer.apply {
                setMediaSource(musicSource.asMediaSource(dataSourceFactory))
                seekTo(currentSongIndex, 0L)
                playWhenReady = playNow
                prepare()
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(EMPTY_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when(parentId) {
            EMPTY_ROOT -> {
                val isResultSent = musicSource.whenReady { isInitialized ->
                    if(isInitialized) {
                        result.sendResult(musicSource.asMediaItems())
                        if(!isPlayerInitialized && musicSource.songs.isNotEmpty()) {
                            preparePlayer(
                                songs = musicSource.songs,
                                songToPlay = musicSource.songs[0],
                                playNow = false
                            )
                            isPlayerInitialized = true
                        }
                    } else {
                        mediaSession.sendSessionEvent(PERMISSION_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if(!isResultSent) {
                    result.detach()
                }
            }
        }
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {

        // TODO setup queue
        private val mainQueue: MutableList<Int> = mutableListOf()
        private val nearestQueue: MutableList<Int> = mutableListOf()


        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            Log.e("MusicQueueNavigator", "windowIndex: $windowIndex")
            return if (windowIndex < musicSource.songs.size) {
                musicSource.songs[windowIndex].description
            } else
                musicSource.songs.last().description
        }

        fun addToMainQueue(indexQueue: List<Int>) {
            mainQueue.addAll(indexQueue)
        }

        fun addToNearestQueue(index: Int) {
            with(nearestQueue) {
                if(contains(index)) {
                    removeAt(indexOf(index))
                }
                add(index)
            }
        }
    }
}
