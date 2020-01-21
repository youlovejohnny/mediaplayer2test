package com.megastar.firebasetest

import android.os.Looper
import java.util.concurrent.Executor

class Executor : Executor {
    override fun execute(p0: Runnable) {
        android.os.Handler(Looper.getMainLooper()).postDelayed(p0, 0)
    }
}