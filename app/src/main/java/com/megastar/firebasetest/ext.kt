package com.megastar.firebasetest

import android.view.View
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata

fun MediaItem?.getSongName(): String? {
    if (this == null) return null
    if (this.metadata == null) return null
    return this.metadata?.getString((MediaMetadata.METADATA_KEY_DISPLAY_TITLE))
}

fun MediaItem?.getArtist(): String? {
    if (this == null) return null
    if (this.metadata == null) return null
    return this.metadata?.getString((MediaMetadata.METADATA_KEY_ARTIST))
}

fun MediaItem?.getDuration(): Long? {
    if (this == null) return null
    if (this.metadata == null) return null
    return this.metadata?.getLong((MediaMetadata.METADATA_KEY_DURATION))
}

fun View?.visibleOrGone(isVisible: Boolean) {
    if (isVisible) {
        this?.visibility = View.VISIBLE
    } else {
        this?.visibility = View.GONE
    }
}