package com.megastar.firebasetest

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_file.view.*

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val fileNameTextView = itemView.fileNameTextView as TextView
    val root = itemView.root as ConstraintLayout

    fun bind(file: SongListItem) {
        fileNameTextView.text = file.name
    }
}