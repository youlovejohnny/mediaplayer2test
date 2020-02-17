package com.megastar.firebasetest

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.AudioAttributesCompat
import androidx.media2.common.FileMediaItem
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.player.MediaPlayer
import androidx.media2.session.MediaSession
import androidx.media2.session.MediaSessionService
import androidx.media2.session.SessionCommandGroup
import kotlin.random.Random


class MediaPlaybackService : MediaSessionService() {

    private val LOG_TAG = "MediaPlaybackService"
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var controllerInfo: MediaSession.ControllerInfo
    private var id = ""

    override fun onCreate() {
        super.onCreate()
        createPlayer()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        this.controllerInfo = controllerInfo
        return mediaSession
    }

    inner class SessionServiceCallback : MediaSession.SessionCallback() {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): SessionCommandGroup? {
            Log.d(LOG_TAG, "onConnect with mediaSession " + mediaSession.id)

            val songs =
                getAllAudioFromDevice(this@MediaPlaybackService)!!


            val mediaItemList = mutableListOf<FileMediaItem>()
            for (song in songs) {
                mediaItemList.add(song.toMediaItem())
            }

            mediaPlayer.setAudioAttributes(AudioAttributesCompat.Builder().setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC).
                setUsage(AudioAttributesCompat.USAGE_MEDIA).build())
            mediaPlayer.setPlaylist(
                mediaItemList as List<MediaItem>,
                MediaMetadata.Builder().putText(
                    MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                    "Супер плейлист"
                ).build()
            )

            mediaPlayer.prepare()

            return super.onConnect(session, controller)
        }

    }

    private fun createPlayer() {
        mediaPlayer = MediaPlayer(this)
        createMediaSession()
    }


    private fun createMediaSession() {
        id = Random.nextInt().toString()
        mediaSession = MediaSession.Builder(this, mediaPlayer)
            .setSessionCallback(ContextCompat.getMainExecutor(this), SessionServiceCallback())
            .setSessionActivity(PendingIntent.getActivity(this,0,Intent(this,PlayerActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT))
            .setId(id)
            .build()

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


    private fun getAllAudioFromDevice(context: Context): MutableList<SongListItem>? {
        val tempAudioList: MutableList<SongListItem> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.MediaColumns.DURATION
        )
        val c = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        if (c != null) {
            while (c.moveToNext()) {
                val path: String = c.getString(0)
                val album: String = c.getString(1)
                val artist: String = c.getString(2)
                val duration: Long = c.getLong(3)
                val name = path.substring(path.lastIndexOf("/") + 1)

                val audioModel = SongListItem(name, artist, album, path,duration)
                if (path.endsWith("mp3"))
                    tempAudioList.add(audioModel)
            }
            c.close()
        }
        return tempAudioList
    }
}