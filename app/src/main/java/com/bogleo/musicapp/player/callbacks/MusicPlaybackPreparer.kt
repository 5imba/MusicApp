package com.bogleo.musicapp.player.callbacks

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bogleo.musicapp.player.MusicSource
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MusicPlaybackPreparer(
    private val musicSource: MusicSource,
    private val onPlayerPrepare: (MediaMetadataCompat?) -> Unit
) : MediaSessionConnector.PlaybackPreparer {

    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        musicSource.whenReady {
            val songToPlay = musicSource.songs.find {
                mediaId == it.description.mediaId
            }
            onPlayerPrepare(songToPlay)
        }
    }

    override fun onPrepare(playWhenReady: Boolean) { }
    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) { }
    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) { }
}