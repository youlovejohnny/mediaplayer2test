package com.megastar.firebasetest.ui.main.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.firebase.storage.StorageReference
import com.megastar.firebasetest.MainActivity
import com.megastar.firebasetest.R
import com.megastar.firebasetest.ui.main.list.adapter.FileAdapter
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(R.layout.fragment_list) {

    lateinit var viewModel: ListViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this)[ListViewModel::class.java]

        viewModel.liveData.observe(this, Observer { onUpdate(it) })

        viewModel.loadData()


    }

    private fun onUpdate(state: ListViewState) {
        when (state) {
            is ListViewState.OnProgress -> {
                if (state.inProgress) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }

            is ListViewState.DataLoaded -> {
                val adapter = FileAdapter(::navigateToPlayer)
                adapter.items.addAll(state.items)
                recyclerView.adapter = adapter
            }

            is ListViewState.OnLoadError -> {
                showMessage("Ошибка загрузки")
            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToPlayer(file: StorageReference) {
        val mainActivity = activity as MainActivity
        mainActivity.navigateToPlayer(file)
    }

}