package com.megastar.firebasetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.MediaMetadata.METADATA_KEY_MEDIA_URI
import androidx.media2.common.MediaMetadata.METADATA_KEY_TITLE
import androidx.media2.player.MediaPlayer
import androidx.media2.session.MediaSession
import androidx.media2.session.MediaSessionService
import androidx.media2.session.SessionCommandGroup
import java.util.*


class MediaPlaybackService : MediaSessionService() {


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        this.controllerInfo = controllerInfo
        return mediaSession
    }


    private val LOG_TAG = "MediaPlaybackService"
    private var mediaSession: MediaSession? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var controllerInfo: MediaSession.ControllerInfo

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, " Service created ")
        mediaPlayer = MediaPlayer(this@MediaPlaybackService).apply {


                        val list = mutableListOf<MediaItem>()
            list.add(MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder()
                        .putText(METADATA_KEY_TITLE,"Мандаринка")
                        .putText(METADATA_KEY_MEDIA_URI,"https://pv-music.com/public/download.php?id=186846466_456240555_770_1e74dd4b916d988916_1e74dd4b916d988916&hash=50bce919711ae0a24a622449020b0e84840831bc5052d3d48ca2d388993b7d51").build()).build())

            setPlaylist(list,MediaMetadata.Builder().putText(METADATA_KEY_TITLE,"Топ плейлист").build())


        }


        mediaSession = MediaSession.Builder(this, mediaPlayer)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, PlayerActivity::class.java),
                    0
                )
            )
            .setId(Random().nextInt().toString())
            .setSessionCallback(ContextCompat.getMainExecutor(this), callback)
            .build()
    }

    val callback = object : MediaSession.SessionCallback() {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): SessionCommandGroup? {
            return super.onConnect(session, controller)
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            super.onDisconnected(session, controller)
        }

        override fun onPlayFromUri(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            uri: Uri,
            extras: Bundle?
        ): Int {
            return super.onPlayFromUri(session, controller, uri, extras)
        }
//
//        override fun onPlay() {
//            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,0,1f).build())
//            mediaSession?.isActive  = true
//            mediaPlayer.play()
//
//            runNotification()
//        }
//
//        override fun onPause() {
//            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED,0,1f).build())
//            mediaPlayer.pause()
//            stopForeground(false)
//            runNotification(false)
//        }
//
//        override fun onStop() {
//            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_STOPPED,0,1f).build())
//            mediaPlayer.pause()
//        }
//


    }


    private fun runNotification(foreground: Boolean = true) {
        val controller = mediaSession!!.connectedControllers[0]
//        val mediaMetadata = controller!!.metadata


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel", "My channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My channel description"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(false)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "channel").apply {
            // Add the metadata for the currently playing track
            setContentTitle("Какое-то название")
            setContentText("Какое-то описание")
//            setSubText(description.description)
//            setLargeIcon(description.iconBitmap)

            // Enable launching the player by clicking the notification
//            setContentIntent(controller.sessionActivity)

            setChannelId("my_channel")
            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaPlaybackService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Add an app icon and set its accent color
            // Be careful about the color
            setSmallIcon(R.drawable.ic_play)
            color = ContextCompat.getColor(this@MediaPlaybackService, R.color.colorPrimary)


            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_prev,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaPlaybackService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            // Add a pause button
//            if (mediaPlayer.isPlaying) {
//            addAction(
//                NotificationCompat.Action(
//                    R.drawable.ic_pause,
//                    "Pause",
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        this@MediaPlaybackService,
//                        PlaybackStateCompat.ACTION_PAUSE
//                    )
//                )
//            )
//            } else {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_play,
                        "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MediaPlaybackService,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                    )
                )
//            }

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_next,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaPlaybackService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
            // Take advantage of MediaStyle features
//            setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSession?.token)
//                .setShowActionsInCompactView(0,1,2)
//
//                // Add a cancel button
//                .setShowCancelButton(true)
//                .setCancelButtonIntent(
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        this@MediaPlaybackService,
//                        PlaybackStateCompat.ACTION_STOP
//                    )
//                )
//
//            )
        }

        if (foreground) {
            startForeground(5755, builder.build())
        } else {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(5755,builder.build())
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

    intent?.let {
        when {
            KeyEventHelper.isPlayEvent(intent) -> {
                mediaPlayer.play().addListener(Runnable { Log.d(LOG_TAG,"PLAY") },ContextCompat.getMainExecutor(this))

//                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,0,1f).build())
//                runNotification()
                ContextCompat.startForegroundService(this, Intent(applicationContext, this.javaClass))
            }

            KeyEventHelper.isPauseEvent(intent) -> {
                mediaPlayer.pause()
//                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED,0,1f).build())
                stopForeground(false)
//                runNotification(false)
                stopSelf()
            }
            else -> {

            }
        }
    }



        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUpdateNotification(session: MediaSession): MediaNotification? {
        return super.onUpdateNotification(session)
    }

}