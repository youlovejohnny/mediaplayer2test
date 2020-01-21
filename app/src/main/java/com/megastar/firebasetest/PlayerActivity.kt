package com.megastar.firebasetest

import android.content.*
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var mediaController: MediaController

    private val connectionCallback = MyConnectionCallback()


    lateinit var name: String
    lateinit var uri: String
    lateinit var sessionToken: SessionToken


    val TAG = "PlayerActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionToken = SessionToken(this,ComponentName(this, MediaPlaybackService::class.java))
        mediaBrowser = MediaBrowser.Builder(this)
            .setSessionToken(sessionToken)
            .setControllerCallback(
               Executor()
            ,connectionCallback)
            .build()


        name = intent.getStringExtra("name")!!
        uri = intent.getStringExtra("uri")!!

        SelectedSong(this@PlayerActivity).selectedSongUrl = uri

        val ids = mutableListOf<String>()
        ids.add("1")
        mediaController.setPlaylist(ids,MediaMetadata.Builder().
            build())



    }


    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    inner class MyConnectionCallback : MediaBrowser.BrowserCallback() {
        override fun onDisconnected(controller: MediaController) {

        }

        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            super.onConnected(controller, allowedCommands)
            mediaController = MediaController.Builder(this@PlayerActivity).setSessionToken(sessionToken).build()
            buildTransportControls()
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            super.onPlayerStateChanged(controller, state)
            setPlayButtonsUi(mediaController.playerState)
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            super.onCurrentMediaItemChanged(controller, item)
        }

}

    fun buildTransportControls() {
        setPlayButtonsUi(mediaController.playerState)
        playButton.setOnClickListener {
            when (mediaController.playerState) {
                SessionPlayer.PLAYER_STATE_PLAYING -> {
                    mediaController.pause()
                }
                PlaybackStateCompat.STATE_NONE-> {
                    mediaController.play()
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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

}