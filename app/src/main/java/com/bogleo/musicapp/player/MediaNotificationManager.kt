package com.bogleo.musicapp.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bogleo.musicapp.R
import com.bogleo.musicapp.common.NOTIFICATION_CHANNEL_ID
import com.bogleo.musicapp.common.NOTIFICATION_ID
import com.bogleo.musicapp.common.utils.getBitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager



class MediaNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val mediaController = MediaControllerCompat(context, sessionToken)

    private val notificationManager: PlayerNotificationManager = PlayerNotificationManager.Builder(
        context,
        NOTIFICATION_ID,
        NOTIFICATION_CHANNEL_ID,
    )
        .setChannelNameResourceId(R.string.notification_channel_name)
        .setChannelDescriptionResourceId(R.string.notification_channel_description)
        .setNotificationListener(notificationListener)
        .setMediaDescriptionAdapter(DescriptionAdapter(mediaController = mediaController))
        .build().apply {
            setSmallIcon(R.drawable.ic_default_album_cover)
            setMediaSessionToken(sessionToken)
        }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class  DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ) : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return  mediaController.metadata.description.description.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return context.getBitmap(mediaController.metadata.description.iconUri)
                ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_album_cover)
        }
    }
}