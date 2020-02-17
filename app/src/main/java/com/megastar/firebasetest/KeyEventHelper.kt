package com.megastar.firebasetest

import android.content.Intent
import android.view.KeyEvent

object KeyEventHelper {
    fun isPlayEvent(intent: Intent): Boolean {
        return isEvent(intent, KeyEvent.KEYCODE_MEDIA_PLAY)
    }

    fun isPauseEvent(intent: Intent): Boolean {
        return isEvent(intent, KeyEvent.KEYCODE_MEDIA_PAUSE)
    }

    fun isStopEvent(intent: Intent): Boolean {
        return isEvent(intent, KeyEvent.KEYCODE_MEDIA_STOP)
    }

    fun isNextEvent(intent: Intent): Boolean {
        return isEvent(intent,KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun isPrevEvent(intent: Intent): Boolean {
        return isEvent(intent,KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    private fun isEvent(intent: Intent, keyEvent: Int): Boolean {
        return intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)?.keyCode == KeyEvent(
            KeyEvent.ACTION_DOWN,
            keyEvent
        ).keyCode
    }
}