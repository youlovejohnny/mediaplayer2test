package com.megastar.firebasetest.ui.main.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage


class ListViewModel : ViewModel() {

    val liveData = MutableLiveData<ListViewState>()

    val storage = FirebaseStorage.getInstance("gs://fir-test-8a00b.appspot.com")

    fun loadData() {
        liveData.value = ListViewState.OnProgress(true)

        storage.reference.listAll().addOnCompleteListener {
            if (it.isSuccessful) {
                liveData.value =
                    ListViewState.DataLoaded(it.result?.items!!)
                liveData.value =
                    ListViewState.OnProgress(false)
            } else {
                liveData.value = ListViewState.OnLoadError
            }
        }
    }
}
