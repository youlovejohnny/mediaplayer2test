package com.megastar.firebasetest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.megastar.firebasetest.ui.main.auth.AuthFragment
import com.megastar.firebasetest.ui.main.list.ListFragment

class MainActivity : AppCompatActivity(), Router {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            navigateToAuth()
        } else {
            navigateToList()
        }
        request()
    }

    override fun navigateToAuth() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AuthFragment.newInstance())
            .commitNow()
    }

    override fun navigateToList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment())
            .commitNow()
    }

    override fun navigateToPlayer(file: StorageReference) {
        val intent = Intent(this, PlayerActivity::class.java)

        file.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                val uri = it.result.toString()
                intent.putExtra("uri", uri)
            }
            intent.putExtra("name", file.name)
            startActivity(intent)
            finish()
        }

    }

    fun request() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission non granted")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                29241)
        } else {

            Log.d("MainActivity", "Permission granted")
        }
    }
}
