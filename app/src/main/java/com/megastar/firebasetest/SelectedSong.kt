package com.megastar.firebasetest

import android.content.Context

class SelectedSong(val context: Context) {

    val sharedPreferences = context.getSharedPreferences("SELECTED_SONG",0)

    var selectedSongUrl: String
        get() {
            return sharedPreferences.getString("URL","")!!
        }
        set(value) {
        sharedPreferences.edit().putString("URL",value).apply()
    }
}