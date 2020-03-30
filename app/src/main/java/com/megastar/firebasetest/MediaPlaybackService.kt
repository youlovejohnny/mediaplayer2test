package com.megastar.firebasetest

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.AudioAttributesCompat
import androidx.media2.common.FileMediaItem
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.player.MediaPlayer
import androidx.media2.player.MediaPlayer2
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import androidx.media2.session.MediaSessionService
import androidx.media2.session.SessionCommandGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random


class MediaPlaybackService : MediaLibraryService() {

    companion object {
        const val SET_PLAYLIST_INTENT = "media.player.set.playlist"
    }

    private val LOG_TAG = "MediaPlaybackService"
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var controllerInfo: MediaSession.ControllerInfo
    private var sessionId = ""
    private var broadcastReceiver: ServiceBroadcastReceiver? = null
    private val currentPlayList = ArrayList<SongListItem>()

    private val intentFilter = IntentFilter(SET_PLAYLIST_INTENT)

    override fun onCreate() {
        super.onCreate()
        createPlayer()
        broadcastReceiver = ServiceBroadcastReceiver()
        registerReceiver(broadcastReceiver,intentFilter)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        this.controllerInfo = controllerInfo
        val session = createMediaSession()
        sessionId = session.id
        return session
    }


    inner class SessionServiceCallback : MediaLibrarySession.MediaLibrarySessionCallback() {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): SessionCommandGroup? {
            sessionId = mediaSession.id
            return super.onConnect(session, controller)
        }

    }

    private fun createPlayer() {
        mediaPlayer = MediaPlayer(this)
        mediaPlayer.setAudioAttributes(
            AudioAttributesCompat.Builder().setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC).setUsage(
                AudioAttributesCompat.USAGE_MEDIA
            ).build()
        )
    }


    private fun createMediaSession(): MediaLibrarySession {
        mediaSession = MediaLibrarySession.Builder(this,
            mediaPlayer,
            ContextCompat.getMainExecutor(this),
            SessionServiceCallback()
        ).setSessionActivity(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, NewPlayerActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
            .setId("5651651")
            .build()

        return mediaSession
    }


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when {
                KeyEventHelper.isPlayEvent(intent) -> {
                    mediaPlayer.play()
                }

                KeyEventHelper.isPauseEvent(intent) -> {
                    mediaPlayer.pause()
                }

                KeyEventHelper.isNextEvent(intent) -> {
                    mediaPlayer.pause()
                    mediaPlayer.skipToNextPlaylistItem()
                    mediaPlayer.play()
                }

                KeyEventHelper.isPrevEvent(intent) -> {
                    mediaPlayer.pause()
                    mediaPlayer.skipToPreviousPlaylistItem()
                    mediaPlayer.play()
                }

                else -> {

                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    inner class ServiceBroadcastReceiver: BroadcastReceiver(){

        override fun onReceive(p0: Context?, p1: Intent?) {

            val string = p1?.getStringExtra("set.playlist")
            val playList = Gson().fromJson<ArrayList<SongListItem>>(string, object : TypeToken<ArrayList<SongListItem>>() {}.type)

            val mediaItemList = mutableListOf<FileMediaItem>()
            for (song in playList) {
                mediaItemList.add(song.toFileMediaItem()!!)
            }

            if (!PlaylistHelper.isEqual(currentPlayList,playList)) {
                mediaPlayer.setPlaylist(
                    mediaItemList as List<MediaItem>,
                    MediaMetadata.Builder().putText(
                        MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                        "Супер плейлист"
                    ).build()
                )
                mediaPlayer.prepare()
                currentPlayList.clear()
                currentPlayList.addAll(playList)
            } else {

            }
        }

    }

    override fun onDestroy() {
        mediaPlayer.close()
        super.onDestroy()
    }
}