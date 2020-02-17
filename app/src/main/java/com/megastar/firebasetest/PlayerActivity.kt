package com.megastar.firebasetest

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionToken
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity(R.layout.activity_player) {

    private lateinit var mediaBrowser: MediaBrowser
    private var mediaController: MediaController? = null
    private lateinit var adapter: FileAdapter
    private val connectionCallback = MyConnectionCallback()

    lateinit var sessionToken: SessionToken


    val TAG = "PlayerActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionToken = SessionToken(this,ComponentName(this, MediaPlaybackService::class.java))
        mediaBrowser = MediaBrowser.Builder(this)
            .setSessionToken(sessionToken)
            .setControllerCallback(
               ContextCompat.getMainExecutor(this)
            ,connectionCallback)
            .build()


        adapter = FileAdapter(::playSong)
        recyclerView.adapter = adapter

    }


    inner class MyConnectionCallback : MediaBrowser.BrowserCallback() {
        override fun onDisconnected(controller: MediaController) {

        }

        @SuppressLint("RestrictedApi")
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            super.onConnected(controller, allowedCommands)
            if (mediaController == null) {
                mediaController = MediaController.Builder(this@PlayerActivity)
                    .setControllerCallback(ContextCompat.getMainExecutor(this@PlayerActivity), controllerCallback)
                    .setSessionToken(sessionToken).build()

            updateSeekBar(mediaController!!)

            }
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            super.onPlayerStateChanged(controller, state)
            mediaController?.let {setPlayButtonsUi(it.playerState) }
            if (mediaController?.playerState == SessionPlayer.PLAYER_STATE_PLAYING) {
                setNowPlayingSong(mediaController!!.currentMediaItemIndex)
                setTrackInfo(mediaController!!.currentMediaItem)
            }
        }

}

    private fun updateSeekBar(mediaController: MediaController) {
        Handler().postDelayed({
            songSeekBar.progress = mediaController.currentPosition.toInt()
            updateSeekBar(mediaController)
        },1000)
    }

    private val controllerCallback = object : MediaController.ControllerCallback() {

        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            super.onConnected(controller, allowedCommands)
            buildTransportControls()
        }
        override fun onPlaylistChanged(
            controller: MediaController,
            list: MutableList<MediaItem>?,
            metadata: MediaMetadata?
        ) {
            list?.let {
                val songlist = mutableListOf<SongListItem>()
                for (item in it) {
                    val songListItem = SongListItem.fromMediaItem(item)
                    songlist.add(songListItem)

            }

                adapter.items.clear()
                adapter.items.addAll(songlist)
                adapter.notifyDataSetChanged()
            }

            super.onPlaylistChanged(controller, list, metadata)
        }

    }

    fun buildTransportControls() {
        mediaController?.let {
            setPlayButtonsUi(it.playerState)
        }
        playButton.setOnClickListener {
            when (mediaController?.playerState) {
                SessionPlayer.PLAYER_STATE_PLAYING -> {
                    mediaController?.pause()
                }
                SessionPlayer.PLAYER_STATE_IDLE,SessionPlayer.PLAYER_STATE_PAUSED-> {
                    mediaController?.play()
                }
            }
        }

        nextButton.setOnClickListener {
            mediaController?.pause()
            mediaController?.skipToNextPlaylistItem()
            mediaController?.play()
        }
        prevButton.setOnClickListener {
            mediaController?.pause()
            mediaController?.skipToNextPlaylistItem()
            mediaController?.play()
        }
    }

    private fun setPlayButtonsUi(state: Int) {
        when(state) {
            SessionPlayer.PLAYER_STATE_IDLE -> {
                playButton.setImageResource(R.drawable.ic_play)
            }
            SessionPlayer.PLAYER_STATE_PAUSED -> {
                playButton.setImageResource(R.drawable.ic_play)
            }

            SessionPlayer.PLAYER_STATE_PLAYING -> {
                playButton.setImageResource(R.drawable.ic_pause)
            }

        }
    }

    private fun setTrackInfo(mediaItem: MediaItem?) {
        mediaItem?.let {
            nameTextView.text = it.getSongName()
            songSeekBar.max = it.getDuration()?.toInt() ?: 0
        } ?: kotlin.run {  }
    }

    private fun setNowPlayingSong(songIndex: Int) {
        adapter.setNowPlaying(songIndex)
    }

    private fun playSong(song: SongListItem) {
        val index = adapter.items.indexOf(song)
        if (mediaController?.currentMediaItemIndex == index) {
            if (mediaController?.playerState == SessionPlayer.PLAYER_STATE_PLAYING) {
                mediaController?.pause()
            } else {
                mediaController?.play()
            }

        } else {
            mediaController?.skipToPlaylistItem(index)
            mediaController?.prepare()
            mediaController?.play()
            setTrackInfo(mediaController?.currentMediaItem)
        }



    }

}


