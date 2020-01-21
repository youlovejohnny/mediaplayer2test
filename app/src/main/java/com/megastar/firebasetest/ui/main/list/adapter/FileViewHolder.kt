package com.megastar.firebasetest.ui.main.list.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_file.view.*

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val fileNameTextView = itemView.fileNameTextView as TextView
    val root = itemView.root as ConstraintLayout

    fun bind(file: StorageReference) {
        fileNameTextView.text = file.name
    }
}