package com.megastar.firebasetest

object PlaylistHelper {

    fun isEqual(
        firstPlaylist: ArrayList<SongListItem>,
        secondPlaylist: ArrayList<SongListItem>
    ): Boolean {
        if (firstPlaylist.size != secondPlaylist.size) return false
        if (firstPlaylist.size == 0 && secondPlaylist.size == 0) return  false
        for (index in 0..firstPlaylist.size) {
            if (firstPlaylist[index].name == secondPlaylist[index].name) {

            } else {
                return false
            }
        }
        return true
    }
}