package com.megastar.firebasetest

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_file.view.*

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val fileNameTextView = itemView.fileNameTextView as TextView
    private val nowPlayingImageView = itemView.nowPlayingImageView

    val root = itemView.root as ConstraintLayout

    fun bind(song: SongListItem) {
        fileNameTextView.text = song.name
        nowPlayingImageView.visibleOrGone(song.isPlayingNow)
    }
}