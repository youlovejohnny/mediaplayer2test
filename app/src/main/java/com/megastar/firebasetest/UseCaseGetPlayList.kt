package com.megastar.firebasetest

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class UseCaseGetPlayList(private val context: Context) {

    fun execute(): ArrayList<SongListItem>{
        val tempAudioList: ArrayList<SongListItem> = ArrayList()
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