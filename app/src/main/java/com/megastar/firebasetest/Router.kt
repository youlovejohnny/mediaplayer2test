package com.megastar.firebasetest

import com.google.firebase.storage.StorageReference

interface Router {
    fun navigateToAuth()
    fun navigateToList()
    fun navigateToPlayer(file: StorageReference)
}