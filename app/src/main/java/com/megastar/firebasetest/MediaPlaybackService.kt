package com.megastar.firebasetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import androidx.media2.common.MediaItem
import androidx.media2.player.MediaPlayer


class MediaPlaybackService : MediaBrowserServiceCompat() {

    //    provides the ability for a client to build and display a menu of the MediaBrowserService's content hierarchy
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }


    private val LOG_TAG = "MediaPlaybackService"
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, " Service created ")
        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {


            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE
                )

            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(SessionServiceCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)

            mediaPlayer = MediaPlayer(this@MediaPlaybackService).apply {

//                setPlaylist()

                prepare()
            }


        }
    }

    inner class SessionServiceCallback: MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {

            return super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onPlay() {
            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,0,1f).build())
            mediaSession?.isActive  = true
            mediaPlayer.play()
            runNotification()
        }

        override fun onPause() {
            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED,0,1f).build())
            mediaPlayer.pause()
            stopForeground(false)
            runNotification(false)
        }

        override fun onStop() {
            mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_STOPPED,0,1f).build())
            mediaPlayer.pause()
        }

    }


    private fun runNotification(foreground: Boolean = true) {
        val controller = mediaSession!!.controller
        val mediaMetadata = controller!!.metadata
//        val description = mediaMetadata!!.description


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
            setContentIntent(controller.sessionActivity)

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
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0,1,2)

                // Add a cancel button
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaPlaybackService,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )

            )
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
                mediaPlayer.play()
                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,0,1f).build())
                runNotification()
                ContextCompat.startForegroundService(this, Intent(applicationContext, this.javaClass))
            }

            KeyEventHelper.isPauseEvent(intent) -> {
                mediaPlayer.pause()
                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED,0,1f).build())
                stopForeground(false)
                runNotification(false)
                stopSelf()
            }
            else -> {

            }
        }
    }


        return super.onStartCommand(intent, flags, startId)
    }

}