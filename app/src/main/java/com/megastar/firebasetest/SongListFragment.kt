package com.megastar.firebasetest

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_player.nameTextView
import kotlinx.android.synthetic.main.activity_player.nextButton
import kotlinx.android.synthetic.main.activity_player.playButton
import kotlinx.android.synthetic.main.activity_player.prevButton
import kotlinx.android.synthetic.main.fragment_song_list.recyclerView
import kotlinx.android.synthetic.main.fragment_song_list.songSeekBar

class SongListFragment : Fragment(R.layout.fragment_song_list) {
    val adapter by lazy { FileAdapter(::playSong) }
    private lateinit var mediaBrowser: MediaBrowser
    private var mediaController: MediaController? = null
    private val connectionCallback = MyConnectionCallback()

    lateinit var sessionToken: SessionToken


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(UseCaseGetPlayList(activity!!).execute()) {
            adapter.items = this
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        playButton.setOnClickListener {
            onPlayButtonClick()
        }

    }


    @SuppressLint("RestrictedApi")
    private fun onPlayButtonClick() {
        val componentName = ComponentName(activity!!, MediaPlaybackService::class.java)
        sessionToken =
            SessionToken(activity!!,componentName )


        mediaBrowser = MediaBrowser.Builder(activity!!)
            .setSessionToken(sessionToken)
            .setControllerCallback(
                ContextCompat.getMainExecutor(activity!!)
                , connectionCallback
            )
            .build()


    }

    inner class MyConnectionCallback : MediaBrowser.BrowserCallback() {
        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            super.onChildrenChanged(browser, parentId, itemCount, params)
        }

        override fun onSearchResultChanged(
            browser: MediaBrowser,
            query: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            super.onSearchResultChanged(browser, query, itemCount, params)
        }

        override fun onDisconnected(controller: MediaController) {

        }

        @SuppressLint("RestrictedApi")
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            super.onConnected(controller, allowedCommands)
            if (mediaController == null) {
                mediaController = MediaController.Builder(this@SongListFragment.activity!!)
                    .setControllerCallback(
                        ContextCompat.getMainExecutor(this@SongListFragment.activity!!),
                        controllerCallback
                    )
                    .setSessionToken(sessionToken).build()

                updateSeekBar(mediaController!!)
                Log.d("SongListFragment","Выполнено за " + mediaBrowser.getLibraryRoot(null).addListener(
                    Runnable { },ContextCompat.getMainExecutor(activity)

            }
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            super.onPlayerStateChanged(controller, state)
            mediaController?.let { setPlayButtonsUi(it.playerState) }
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
        }, 1000)
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
                SessionPlayer.PLAYER_STATE_IDLE, SessionPlayer.PLAYER_STATE_PAUSED -> {
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
        when (state) {
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
        } ?: kotlin.run { }
    }

    private fun setNowPlayingSong(songIndex: Int) {
        adapter.setNowPlaying(songIndex)
    }

    private fun playSong(song: SongListItem, playList: ArrayList<SongListItem>) {
        sendPlayList(playList)

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


    private fun sendPlayList(playList: ArrayList<SongListItem>) {
        val intent = Intent(MediaPlaybackService.SET_PLAYLIST_INTENT)
        intent.putExtra("set.playlist", Gson().toJson(playList))
        activity?.sendBroadcast(intent)

    }
}