package com.megastar.firebasetest

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
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
               ContextCompat.getMainExecutor(this)
            ,connectionCallback)
            .build()


        name = intent.getStringExtra("name")!!
        uri = intent.getStringExtra("uri")!!

        SelectedSong(this@PlayerActivity).selectedSongUrl = uri

    }

    inner class MyConnectionCallback : MediaBrowser.BrowserCallback() {
        override fun onDisconnected(controller: MediaController) {

        }

        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            super.onConnected(controller, allowedCommands)
            this@PlayerActivity.mediaController = controller
            buildTransportControls()
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            super.onPlayerStateChanged(controller, state)
            setPlayButtonsUi(mediaController.playerState)
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            super.onCurrentMediaItemChanged(controller, item)
        }

        override fun onPlaylistChanged(
            controller: MediaController,
            list: MutableList<MediaItem>?,
            metadata: MediaMetadata?
        ) {
            super.onPlaylistChanged(controller, list, metadata)
        }

        override fun onPlaylistMetadataChanged(
            controller: MediaController,
            metadata: MediaMetadata?
        ) {
            super.onPlaylistMetadataChanged(controller, metadata)
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
                    mediaController.playFromUri(Uri.parse(""),Bundle.EMPTY)
//                    mediaController.playFromUri(Uri.parse("https://pv-music.com/public/download.php?id=186846466_456240555_770_1e74dd4b916d988916_1e74dd4b916d988916&hash=50bce919711ae0a24a622449020b0e84840831bc5052d3d48ca2d388993b7d51"),
//                        Bundle.EMPTY)
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