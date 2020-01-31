package com.megastar.firebasetest

import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import androidx.media2.common.FileMediaItem
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import java.io.File

data class SongListItem(
    val name: String?,
    val artist: String?,
    val album: String?,
    val path: String?

) {
    companion object {
        fun fromMediaItem(mediaItem: MediaItem): SongListItem {
            with(mediaItem.metadata!!) {
                return SongListItem(
                    getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE),
                    getString(MediaMetadata.METADATA_KEY_ARTIST),
                    getString(MediaMetadata.METADATA_KEY_ALBUM),
                    getString(MediaMetadata.METADATA_KEY_MEDIA_URI)
                )
            }
        }


    }

    fun toMediaItem(): FileMediaItem {
        return FileMediaItem.Builder(ParcelFileDescriptor.open(File(path),MODE_READ_ONLY)).setMetadata(
            MediaMetadata.Builder()
                .putText(MediaMetadata.METADATA_KEY_MEDIA_ID,java.util.Random().nextInt().toString())
                .putText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE,name)
                .putText(MediaMetadata.METADATA_KEY_ARTIST, artist)
                .putText(MediaMetadata.METADATA_KEY_ALBUM,album)
                .putText(MediaMetadata.METADATA_KEY_MEDIA_URI, path)
        .build()).build()

    }

}