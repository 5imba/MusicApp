package com.bogleo.musicapp.player.callbacks

import android.widget.Toast
import com.bogleo.musicapp.player.MusicService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.Listener {

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        //if (playbackState == Player.STATE_READY) {
        //    musicService.stopForeground((false))
        //}
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        // TODO add error popup
        Toast.makeText(musicService, "Error", Toast.LENGTH_LONG).show()
    }
}