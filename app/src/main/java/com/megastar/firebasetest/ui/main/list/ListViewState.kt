package com.megastar.firebasetest.ui.main.list

import com.google.firebase.storage.StorageReference

sealed class ListViewState {
    class OnProgress(val inProgress: Boolean): ListViewState()
    class DataLoaded(val items: MutableList<StorageReference>): ListViewState()
    object OnLoadError: ListViewState()
}